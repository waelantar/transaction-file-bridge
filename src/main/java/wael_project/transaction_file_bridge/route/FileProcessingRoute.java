package wael_project.transaction_file_bridge.route;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.minio.MinioConstants;
import org.apache.camel.dataformat.csv.CsvDataFormat;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import wael_project.transaction_file_bridge.processor.TransactionProcessor;

@Component
public class FileProcessingRoute extends RouteBuilder {
    private final TransactionProcessor transactionProcessor;

    @Value("${input.directory}")
    private String inputDir;

    @Value("${error.directory}")
    private String errorDir;

    @Value("${minio.endpoint}")
    private String minioEndpoint;

    @Value("${minio.access-key}")
    private String minioAccessKey;

    @Value("${minio.secret-key}")
    private String minioSecretKey;

    @Value("${minio.bucket-name}")
    private String minioBucketName;

    public FileProcessingRoute(TransactionProcessor transactionProcessor) {
        this.transactionProcessor = transactionProcessor;
    }

    @Override
    public void configure() {
        // Configure CSV data format
        CsvDataFormat csv = new CsvDataFormat()
                .setDelimiter(',')
                .setSkipHeaderRecord(true);

        // Error handler route
        from("direct:errorHandler")
                .routeId("errorHandlerRoute")
                .log("Error processing file: ${file:name}, error: ${exception.message}")
                .to("file:"+errorDir);

        // Main file processing route
        from("file:"+inputDir+"?moveFailed="+errorDir+"&initialDelay=5000&delay=1000")
                .routeId("fileProcessingRoute")
                .setProperty("originalBody", body()) // Store original body for error handling
                .setProperty("originalFileName", simple("${file:name}")) // Store original filename
                .setProperty("fileNameWithoutExt", simple("${file:name.noext}")) // Store filename without extension
                .log("Processing file: ${exchangeProperty.originalFileName}")
                .unmarshal(csv)
                .process(transactionProcessor)
                .setHeader(MinioConstants.OBJECT_NAME, simple("${exchangeProperty.fileNameWithoutExt}-${date:now:yyyyMMddHHmmssSSS}.json")) // Set MinIO object name with timestamp
                .log("Uploading to MinIO with object name: ${header.CamelMinioObjectName}")
                .to("minio://"+minioBucketName+"?endpoint="+minioEndpoint+"&accessKey="+minioAccessKey+"&secretKey="+minioSecretKey
                        +"&autoCreateBucket=true")
                .log("Successfully uploaded to MinIO: ${header.CamelMinioObjectName}");
    }
}
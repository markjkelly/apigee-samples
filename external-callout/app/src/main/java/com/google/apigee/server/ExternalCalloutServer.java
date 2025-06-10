package com.google.apigee.server;

import com.google.apigee.proto.ExternalCallout.MessageContext;
import com.google.apigee.proto.ExternalCallout.Response;
import com.google.apigee.proto.ExternalCallout.Strings;
import com.google.apigee.proto.ExternalCalloutServiceGrpc.ExternalCalloutServiceImplBase;
import io.grpc.Grpc;
import io.grpc.InsecureServerCredentials;
import io.grpc.Server;
import io.grpc.protobuf.services.ProtoReflectionServiceV1;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Server that manages startup/shutdown of a {@code ExternalCalloutServer}
 * server.
 */
public class ExternalCalloutServer {
    private static final Logger logger = Logger.getLogger(ExternalCalloutServer.class.getName());

    private Server server;

    private void start() throws IOException {
        /* The port on which the server should run */
        String portStr = System.getenv("PORT");
        if (portStr == null || portStr.isEmpty()) {
            portStr = "8080";
        }
        int port = Integer.parseInt(portStr);

        server = Grpc.newServerBuilderForPort(port, InsecureServerCredentials.create())
                .addService(new ServerImpl())
                .addService(ProtoReflectionServiceV1.newInstance())
                .build()
                .start();
        logger.info("Server started, listening on " + port);

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                System.err.println("*** shutting down gRPC server since JVM is shutting down");
                try {
                    ExternalCalloutServer.this.stop();
                } catch (InterruptedException e) {
                    e.printStackTrace(System.err);
                }
                System.err.println("*** server shut down");
            }
        });
    }

    private void stop() throws InterruptedException {
        if (server != null) {
            server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
        }
    }

    /**
     * Await termination on the main thread since the grpc library uses daemon
     * threads.
     */
    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    /**
     * Main launches the server from the command line.
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        final ExternalCalloutServer server = new ExternalCalloutServer();
        server.start();
        server.blockUntilShutdown();
    }

    private static class ServerImpl extends ExternalCalloutServiceImplBase {

        @Override
        public void processMessage(
                MessageContext rpcRequest, StreamObserver<MessageContext> responseObserver) {

            logger.info("Content: " + rpcRequest.getRequest().getContent());

            Response response = rpcRequest.getResponse().toBuilder()
                    .setContent("Response from the gRPC server (Java).")
                    .putHeaders("grpc.server.header", Strings.newBuilder().addStrings("Java").build())
                    .build();
            MessageContext rpcResponse = rpcRequest.toBuilder().setResponse(response).build();

            responseObserver.onNext(rpcResponse);
            responseObserver.onCompleted();
        }
    }
}
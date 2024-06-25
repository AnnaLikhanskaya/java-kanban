package handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exceptions.IntersectionsOfTaskIntervalsException;
import exceptions.ManagerSaveException;
import exceptions.NotFoundException;
import model.Task;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static httpServer.HttpTaskServer.gson;

public abstract class BaseHttpHandler<T extends Task> implements HttpHandler {

    protected String firstPartOfPath;
    protected Function<Integer, T> getByIdHandler = (Integer i) -> {
        throw new RuntimeException("Не реализовано");
    };
    protected Consumer<T> createHandler = (T i) -> {
        throw new RuntimeException("Не реализовано");
    };
    protected Consumer<T> updateHandler = (T i) -> {
        throw new RuntimeException("Не реализовано");
    };
    protected Supplier<List<T>> getAllHandler = () -> {
        throw new RuntimeException("Не реализовано");
    };
    protected Consumer<Integer> deleteHandler = (Integer i) -> {
        throw new RuntimeException("Не реализовано");
    };
    protected Class<T> type;

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());
        try {
            switch (endpoint) {
                case GET_BY_ID -> handleGetById(exchange);
                case GET_ALL -> handleGetAll(exchange);
                case POST_CREATE -> handlePostCreate(exchange);
                case POST_UPDATE -> handlePostUpdate(exchange);
                case DELETE -> handleDelete(exchange);
                default -> sendText(exchange, "Такого эндпоинта не существует", 404);
            }
        } catch (ManagerSaveException e) {
            sendText(exchange, e.getMessage(), 406);
        } catch (IntersectionsOfTaskIntervalsException e) {
            sendHasInteractions(exchange, e.getMessage());
        } catch (NotFoundException e) {
            sendNotFound(exchange, e.getMessage());
        } catch (NumberFormatException e) {
            sendText(exchange, "Некорректный id", 400);
        } catch (RuntimeException e) {
            sendText(exchange, e.getMessage(), 500);
        }
    }

    protected void handleGetById(HttpExchange exchange) throws IOException {
        T object = getByIdHandler.apply(getIdFromPath(exchange));
        String response = gson.toJson(object);
        sendText(exchange, response, 200);
    }

    protected void handlePostCreate(HttpExchange exchange) throws IOException {
        T object = gson.fromJson(getStringFromBody(exchange), type);
        createHandler.accept(object);
        sendText(exchange, gson.toJson(object), 201);
    }

    protected void handlePostUpdate(HttpExchange exchange) throws IOException {
        T object = gson.fromJson(getStringFromBody(exchange), type);
        updateHandler.accept(object);
        sendText(exchange, "", 201);
    }

    protected void handleGetAll(HttpExchange exchange) throws IOException {
        String response = gson.toJson(getAllHandler.get());
        sendText(exchange, response, 200);

    }

    protected void handleDelete(HttpExchange exchange) throws IOException {
        deleteHandler.accept(getIdFromPath(exchange));
        sendText(exchange, "", 200);
    }

    protected void sendText(HttpExchange h, String text, int responseCode) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(responseCode, resp.length);
        h.getResponseBody().write(resp);
        h.close();
    }

    protected void sendNotFound(HttpExchange h, String errorMessage) throws IOException {
         byte[] resp = errorMessage.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(404, resp.length);
        h.getResponseBody().write(resp);
        h.close();
    }

    protected void sendHasInteractions(HttpExchange h, String text) throws IOException {
         byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(406, resp.length);
        h.getResponseBody().write(resp);
        h.close();
    }

    protected Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] pathParts = requestPath.split("/");
        if (pathParts[1].equals(firstPartOfPath)) {
            if (pathParts.length == 2) {
                switch (requestMethod) {
                    case "GET" -> {
                        return Endpoint.GET_ALL;
                    }
                    case "POST" -> {
                        return Endpoint.POST_CREATE;
                    }
                }
            }
            if (pathParts.length == 3) {
                switch (requestMethod) {
                    case "GET" -> {
                        return Endpoint.GET_BY_ID;
                    }
                    case "POST" -> {
                        return Endpoint.POST_UPDATE;
                    }
                    case "DELETE" -> {
                        return Endpoint.DELETE;
                    }
                }
            }
        }
        return Endpoint.UNKNOWN;
    }

    protected Integer getIdFromPath(HttpExchange exchange) throws NumberFormatException {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        return Integer.parseInt(pathParts[2]);
    }

    private String getStringFromBody(HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        return new String(inputStream.readAllBytes());
    }
}
package handler;

import com.sun.net.httpserver.HttpExchange;
import exceptions.ManagerSaveException;
import exceptions.NotFoundException;
import model.Epic;

import java.io.IOException;

import static http_server.HttpTaskServer.gson;
import static http_server.HttpTaskServer.taskManager;


public class EpicHandler extends BaseHttpHandler<Epic> {

    public EpicHandler() {
        firstPartOfPath = "epics";
        createHandler = taskManager::createEpic;
        getByIdHandler = taskManager::getEpicById;
        updateHandler = taskManager::updateEpic;
        getAllHandler = taskManager::getEpicAll;
        deleteHandler = taskManager::deleteEpicById;
        type = Epic.class;
    }

    private void handleGetSubTasksList(HttpExchange exchange) throws IOException {
        String response;
        response = gson.toJson(taskManager.getSubTasksByEpic(getIdFromPath(exchange)));
        sendText(exchange, response, 200);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());
        try {
            if (endpoint == Endpoint.GET_SUBTASKS_BY_EPIC) {
                handleGetSubTasksList(exchange);
            } else {
                super.handle(exchange);
            }
        } catch (ManagerSaveException e) {
            sendText(exchange, e.getMessage(), 406);
        } catch (NotFoundException e) {
            sendNotFound(exchange, e.getMessage());
        } catch (NumberFormatException e) {
            sendText(exchange, "Некорректный id", 400);
        } catch (RuntimeException e) {
            sendText(exchange, e.getMessage(), 500);
        }
    }

    @Override
    protected Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] pathParts = requestPath.split("/");
        if (pathParts[1].equals(firstPartOfPath) && pathParts.length == 4 && pathParts[3].equals("subtasks")
                && requestMethod.equals("GET")) {
            return Endpoint.GET_SUBTASKS_BY_EPIC;
        }
        return super.getEndpoint(requestPath, requestMethod);
    }
}
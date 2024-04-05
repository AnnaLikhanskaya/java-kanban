package model;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private List<SubTask> subTasks = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description);
    }

    public Epic(String name, String description, Status status, int id) {
        super(name, status, description, id);
    }

    public List<SubTask> getSubTasks() {
        return subTasks;
    }

    public void setSubTasks(List<SubTask> subTasks) {
        this.subTasks = subTasks;
    }

    public void addSubTask(SubTask subTask) {
        subTasks.add(subTask);
    }

    public void removeSubTask(SubTask subTaskForRemove) {
        for (int i = 0; i < subTasks.size(); i++) {
            if (subTasks.get(i).getId() == subTaskForRemove.getId()) {
                subTasks.remove(i);
                return;

            }
        }
    }

    public void updateStatus() {
        if (calculateStatus(Status.NEW) == subTasks.size()) {
            this.status = Status.NEW;
        } else if (calculateStatus(Status.DONE) == subTasks.size()) {
            this.status = Status.DONE;
        } else {
            this.status = Status.IN_PROGRESS;
        }

    }

    private int calculateStatus(Status status) {
        int count = 0;
        for (SubTask subTask : subTasks) {
            if (subTask.getStatus().equals(status)) {
                count++;
            }
        }
        return count;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subTasks=" + subTasks +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }

    public void removeAllSubTask() {
        subTasks.clear();
    }
}
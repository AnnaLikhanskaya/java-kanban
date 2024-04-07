package model;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private List<Integer> subTasksIds = new ArrayList<>();


    public Epic(String name, String description) {
        super(name, description);
    }

    public Epic(String name, String description, int id) {
        super(name, description, id);
    }

    public List<Integer> getSubTasksIds() {
        return subTasksIds;
    }

    public void setSubTasksIds(List<Integer> subTasksIds) {
        this.subTasksIds = subTasksIds;
    }

    public void addSubTaskId(Integer subTaskId) {
        subTasksIds.add(subTaskId);
    }

    public void removeSubTaskId(int id) {
        for (int i = 0; i < subTasksIds.size(); i++) {
            if (subTasksIds.get(i) == id) {
                subTasksIds.remove(i);
                return;
            }
        }
    }

    public void updateStatus(List<Status> statuses) {

        if (calculateStatusesCount(Status.NEW,statuses) == subTasksIds.size()) {
            this.status = Status.NEW;
        } else if (calculateStatusesCount(Status.DONE, statuses) == subTasksIds.size()) {
            this.status = Status.DONE;
        } else {
            this.status = Status.IN_PROGRESS;
        }
    }


    private int calculateStatusesCount(Status checkingStatus, List<Status> statuses) {
        int count = 0;
        for (Status status : statuses) {
            if (status.equals(checkingStatus)) {
                count++;
            }
        }
        return count;
    }


    @Override
    public String toString() {
        return "Epic{" +
                "subTasks=" + subTasksIds +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }

    public void removeAllSubTask() {
        subTasksIds.clear();
    }
}
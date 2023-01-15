package task_tracker.manager;

import org.jetbrains.annotations.NotNull;
import task_tracker.model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskManager {

    private final Map<Integer, Task> tasks;
    private final Map<Integer, Epic> epics;
    private final Map<Integer, Subtask> subtasks;
    private int id;

    public TaskManager() {
        this.epics = new HashMap<>();
        this.subtasks = new HashMap<>();
        this.tasks = new HashMap<>();
        id = 1;
    }

    //  -- Будет удален
    //  Метод для тестирования (Загрузка данных из файла)
    //  Предполагается, что в файле все ID начинаются с 1 и без пропусков.
    //  Преподалагается, что в файле все данные все коректные.
    public void loadTasks() {
        List<String> fileContents = ConsoleUtil.readFileContents();

        if (fileContents.isEmpty()) System.out.println("Пустой файл");
        else {
            for (String fileContent : fileContents) {
                String[] recordContents = fileContent.split(",");

                switch (recordContents[0]) {
                    case "Epic":
                        Integer idEpic = Integer.parseInt(recordContents[3]);

                        if (epics.containsKey(idEpic))
                            System.out.println("Задача с ID " + idEpic + "уже существует");
                        else {
                            epics.put(id, new Epic(recordContents[1]
                                    , recordContents[2]
                                    , idEpic
                                    , Status.getStatusByName(recordContents[4])));
                            id++;
                        }

                        break;

                    case "Subtask":
                        Integer idSubtask = Integer.parseInt(recordContents[3]);
                        Integer idParentEpic = Integer.parseInt(recordContents[5]);

                        if (subtasks.containsKey(idSubtask))
                            System.out.println("Задача с ID " + idSubtask + "уже существует");
                        else {
                            subtasks.put(id, new Subtask(recordContents[1]
                                    , recordContents[2]
                                    , idSubtask
                                    , Status.getStatusByName(recordContents[4])
                                    , idParentEpic));
                            epics.get(idParentEpic).addSubtasks(idSubtask);
                            id++;
                        }

                        break;

                    case "Task":
                        Integer idTask = Integer.parseInt(recordContents[3]);

                        if (tasks.containsKey(idTask))
                            System.out.println("Задача с ID " + idTask + "уже существует");
                        else {
                            tasks.put(id, new Task(recordContents[1]
                                    , recordContents[2]
                                    , idTask
                                    , Status.getStatusByName(recordContents[4])));
                            id++;
                        }

                        break;

                    default:
                        System.out.println("Ошибка чтения строки: " + fileContent);
                }
            }
        }
    }

    //  -- Будет удален
    //  Метод для тестирования
    //  Измененяет статусы задач
    public void changeStatus() {
        showAllTasks();
        getSubtaskById(2).setStatus(Status.IN_PROGRESS);
        renewSubtask(getSubtaskById(2));
        showAllTasks();
        getSubtaskById(2).setStatus(Status.DONE);
        getSubtaskById(3).setStatus(Status.DONE);
        getSubtaskById(4).setStatus(Status.DONE);
        renewSubtask(getSubtaskById(2));
        renewSubtask(getSubtaskById(3));
        renewSubtask(getSubtaskById(4));
        showAllTasks();
    }

    public void showAllTasks() {
        for (Epic epic : getEpics()) {
            System.out.println(epic.toString());
            for (Subtask task : getSubtasksByEpic(epic)) {
                System.out.println(task.toString());
            }
        }

        for (Task task : getTasks()) {
            System.out.println(task.toString());
        }
    }

    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public void clearTasks() {
        tasks.clear();
    }

    public void clearEpics() {
        epics.clear();
    }

    public void clearSubtasks() {
        subtasks.clear();
    }

    public Task getTaskById(Integer id) {
        return tasks.get(id);
    }

    public Epic getEpicById(Integer id) {
        return epics.get(id);
    }

    public Subtask getSubtaskById(Integer id) {
        return subtasks.get(id);
    }

    public void createTask(Task task) {
        tasks.put(id++, new Task(task));
    }

    public void createEpic(Epic epic) {
        epics.put(id++, new Epic(epic));
    }

    public void createSubtask(Subtask subtask) {
        subtasks.put(id++, new Subtask(subtask));
        setEpicStatusBySubtasks(getEpicById(subtask.getParentEpicId()));
    }

    public void renewTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public void renewEpic(Epic epic) {
        epics.put(epic.getId(), epic);
        setEpicStatusBySubtasks(epic);
    }

    public void renewSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        setEpicStatusBySubtasks(getEpicById(subtask.getParentEpicId()));
    }

    public void deleteTaskById(Integer id) {
        tasks.remove(id);
    }

    public void deleteEpicById(Integer id) {
        for (Integer key : epics.get(id).getSubtasks()) {
            subtasks.remove(key);
        }
        epics.remove(id);
    }

    public void deleteSubtaskById(Integer id) {
        subtasks.remove(id);
    }

    public List<Subtask> getSubtasksByEpic(@NotNull Epic epic) {
        List<Subtask> list = new ArrayList<>();

        for (Integer key : epic.getSubtasks()) {
            list.add(subtasks.get(key));
        }

        return list;
    }

    protected void setEpicStatusBySubtasks(Epic epic) {
        List<Status> list = new ArrayList<>();

        for (Subtask subtask : getSubtasksByEpic(epic)) {
            list.add(subtask.getStatus());
        }

        epic.setStatus(list);
    }
}
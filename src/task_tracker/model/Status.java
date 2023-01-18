package task_tracker.model;

import java.util.HashMap;
import java.util.Map;

public enum Status {

    NEW,
    IN_PROGRESS,
    DONE;

    // Данный блок кода необходим для загрузки данных, так как при загрузке это String,
    // а нам необходимо элемент перечисления.
    // Если есть другой способ получить сопоставление статуса и его строкого представления,
    // то с удовольствием прочитаю статью об этом.
    private static final Map<String, Status> LOOKUP_MAP = new HashMap<>();

    static {
        for (Status status : values()) {
            LOOKUP_MAP.put(status.name(), status);
        }
    }

    public static Status getStatusByName(String name) {
        if (!LOOKUP_MAP.containsKey(name)) return Status.NEW;
        return LOOKUP_MAP.get(name);
    }
}

package com.machopiggies.famedpanic.util;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

public class RepeatingTask {
    private BukkitTask task;
    private int counter;
    private boolean cancelled;

    public void increment() {
        this.counter++;
    }

    public int getCounter() {
        return this.counter;
    }

    public void setTask(BukkitTask task) {
        this.task = task;
    }

    public void cancel() {
        cancelled = true;
        Bukkit.getScheduler().cancelTask(this.task.getTaskId());
    }

    public boolean isCancelled() {
        return cancelled;
    }
}

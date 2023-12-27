package com.comert.gradar2d.plot;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Stream;

public final class PlotPool {

    private static final PlotPool INSTANCE = new PlotPool();

    private final int poolCapacity = 5;
    private final List<Plot> pool = new ArrayList<>(poolCapacity);

    private final Lock locker = new ReentrantLock();

    private PlotPool() {
        for (int i = 0; i < poolCapacity; i++) {
            pool.add(PlotFactory.createPlot());
        }
    }

    public static PlotPool getInstance() {
        return INSTANCE;
    }

    public Plot getDrawnPlot() {
        locker.lock();

        try {
            final Optional<Plot> plotOptional = pool // maybe primitive search instead of stream for performance
                    .stream()
                    .filter(Plot::isDrawn)
                    .findFirst();
            if (plotOptional.isPresent()) {
                return plotOptional.get();
            } else {
                Plot newPlot = PlotFactory.createPlot();
                pool.add(newPlot);
                return newPlot;
            }
        } finally {
            locker.unlock();
        }

    }

    public Optional<Stream<Plot>> getNotDrawnPlots() {
        boolean locked = false;

        try {
            locked = locker.tryLock(35, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            System.out.println(getClass().getSimpleName() + " interrupted");
        }
        try {
            if (locked) {
                return Optional.of(pool
                        .stream()
                        .filter(plot -> !plot.isDrawn()));
            } else {
                return Optional.empty();
            }
        } finally {
            locker.unlock();
        }

    }

    public void printTotalPoolSize() {
        System.out.println(pool.size());
    }

}

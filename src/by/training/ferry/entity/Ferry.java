package by.training.ferry.entity;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Ferry {
    private static final Logger logger = LogManager.getLogger();
    private static final int MAX_WEIGHT = 100;
    private static final int MAX_SQUARE = 500;
    private static final int TIMEOUT = 300;
    private static final int TRANSPORTATION_TIME = 3;
    private List<Car> autosOnFerry;
    private static final Lock locker;
    private static Condition condition;
    private static Ferry ferry;
    private static AtomicInteger currentWeight;
    private static AtomicInteger currentArea;
    private static AtomicBoolean isFerryCreated;

    static {
        locker = new ReentrantLock(true);
        condition = locker.newCondition();
        currentWeight = new AtomicInteger();
        currentArea = new AtomicInteger();
        isFerryCreated = new AtomicBoolean();
    }

    private Ferry(){
        autosOnFerry = new ArrayList<>();
    }

    public static int getMaxWeight() {
        return MAX_WEIGHT;
    }

    public static int getMaxSquare() {
        return MAX_SQUARE;
    }

    public static Ferry getFerry() {
        if (!isFerryCreated.get()) {
            try {
                locker.lock();
                if (ferry == null) {
                    ferry = new Ferry();
                    isFerryCreated.set(true);
                }
            } finally {
                locker.unlock();
            }
        }
        return ferry;
    }

    public static AtomicInteger getCurrentWeight() {
        return currentWeight;
    }

    public static void setCurrentWeight(AtomicInteger currentWeight) {
        Ferry.currentWeight = currentWeight;
    }

    public static AtomicInteger getCurrentArea() {
        return currentArea;
    }

    public static void setCurrentArea(AtomicInteger currentArea) {
        Ferry.currentArea = currentArea;
    }

    public static AtomicBoolean getIsFerryCreated() {
        return isFerryCreated;
    }

    public static void setIsFerryCreated(AtomicBoolean isFerryCreated) {
        Ferry.isFerryCreated = isFerryCreated;
    }

    public boolean isFreeSpaceLeft (Car car){
        long weightWithCar = currentWeight.get() + car.getType().getWeight();
        long squareWithCar = currentArea.get() + car.getType().getSquare();
        return (weightWithCar <= MAX_WEIGHT && squareWithCar <= MAX_SQUARE);
    }

    public void addCar(Car car) {
        try {
            locker.lock();
            while (!(isFreeSpaceLeft(car))) {
                logger.info("Auto " + car.getId() + " is waiting" + car.getType().getSquare());
                condition.await();
            }

            autosOnFerry.add(car);
            currentArea.set(currentArea.addAndGet(car.getType().getSquare()));
            currentWeight.set(currentWeight.addAndGet(car.getType().getWeight()));

            logger.info("Auto " + car.getId() + " is on ferry" + car.getType().getSquare());
            condition.signalAll();
        } catch (InterruptedException e) {
        } finally {
            locker.unlock();
        }
    }

    public void transport() {
        try {
            locker.lock();
            TimeUnit.MILLISECONDS.sleep(TIMEOUT);
            while (this.autosOnFerry.size() > 0) {
                logger.info("Ferry is waiting");
                condition.await();
            }
            TimeUnit.SECONDS.sleep(TRANSPORTATION_TIME);
            int size = autosOnFerry.size();
            for (int i = 0; i < size; i++) {
                logger.info("Auto is transported");
            }
            currentArea.set(0);
            currentWeight.set(0);
            condition.signalAll();
            TimeUnit.MILLISECONDS.sleep(TIMEOUT);
            } catch (InterruptedException e) {
            } finally {
                locker.unlock();
            }
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Ferry ferry = (Ferry) o;

        return autosOnFerry != null ? autosOnFerry.equals(ferry.autosOnFerry) : ferry.autosOnFerry == null;
    }

    @Override
    public int hashCode() {
        return autosOnFerry != null ? autosOnFerry.hashCode() : 0;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Ferry{");
        sb.append("autosOnFerry=").append(autosOnFerry);
        sb.append('}');
        return sb.toString();
    }
}

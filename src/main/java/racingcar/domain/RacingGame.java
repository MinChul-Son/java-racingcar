package racingcar.domain;

import racingcar.domain.movable.RandomForwardStrategy;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import racingcar.view.ResultView;

public class RacingGame {

    private static final int START_FROM_ZERO = 0;

    private final Cars cars;
    private final MovingResult movingResult;
    private final RacingResult racingResult;

    private RacingGame(final Cars cars) {
        this(cars, MovingResult.instance(), RacingResult.instance());
    }

    private RacingGame(final Cars cars, final MovingResult movingResult,
        final RacingResult racingResult) {
        this.cars = cars;
        this.movingResult = movingResult;
        this.racingResult = racingResult;
    }

    public static RacingGame from(final Cars cars) {
        return new RacingGame(cars);
    }

    public void drive(final int racingTime, final ResultView resultView) {
        for (int time = START_FROM_ZERO; time < racingTime; time++) {
            moveAll(resultView);
        }
    }

    private void moveAll(final ResultView resultView) {
        IntStream.range(0, cars.getCars().size())
            .forEach(idx -> {
                cars.getCars().get(idx).moveForward();
                CarStateInRace carState = new CarStateInRace(cars.getCars().get(idx));
                resultView.convertCurrentCarStatement(carState);
                movingResult.storeCurrentRoundStatement(carState);
            });
    }

    public RacingResult judgeWinners() {
        final int maxPosition = maxDriveLength();
        racingResult.registerWinners(cars.getCars().stream()
            .filter(car -> (car.getStep() == maxPosition && car.getStep() > 0))
            .map(Car::getName)
            .collect(Collectors.toList()));

        return racingResult;
    }

    private int maxDriveLength() {
        return cars.getCars().stream()
            .mapToInt(Car::getStep)
            .max()
            .getAsInt();
    }

    public void registerCars(final List<String> splitUserInput) {
        for (String carName : splitUserInput) {
            cars.getCars().add(Car.of(carName, new RandomForwardStrategy()));
        }
    }

    public MovingResult getMovingResult() {
        return movingResult;
    }

}

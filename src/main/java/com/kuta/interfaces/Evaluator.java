package com.kuta.interfaces;

public interface Evaluator {
    public int rateEveryCell(byte[][] schedule);

    public int rateSubjectFrequency(byte[][] schedule);

    public int rateMovingBetweenClassroomsAndFloors(byte[][] schedule);

    public int rateLunchBreak(byte[][] schedule);

    public int rateLessonAmount(byte[][] schedule);

    public int rateLabsTogether(byte[][] schedule);

    public int rateImportantSubjectPlacement(byte[][] schedule);

    public int rateScheduleInterruptions(byte[][] schedule);

    public int rateMultipleLabs(byte[][] schedule);

    public int rateMyWellbeing(byte[][] schedule);

    public int[] rateSchedule(byte[][] schedule);

    public void handleSchedule(byte[][] schedule);
}

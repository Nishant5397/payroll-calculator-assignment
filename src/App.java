
import java.text.ParseException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;



class Day {
    private String date;
    private String timeIn;
    private String timeOut;

    public Day(String date, String timeIn, String timeOut) {
        this.date = date;
        this.timeIn = timeIn;
        this.timeOut = timeOut;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTimeIn() {
        return timeIn;
    }

    public void setTimeIn(String timeIn) {
        this.timeIn = timeIn;
    }

    public String getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(String timeOut) {
        this.timeOut = timeOut;
    }

    public LocalDateTime getFormattedTimeIn() throws ParseException {
        return LocalDateTime.parse(date + " " + timeIn, DateTimeFormatter.ofPattern("d-MMM-yy h:mm a"));
    }

    public LocalDateTime getFormattedTimeOut() throws ParseException {
        return LocalDateTime.parse(date + " " + timeOut, DateTimeFormatter.ofPattern("d-MMM-yy h:mm a"));
    }

}

class Schedule {
    private List<Day> days;

    public Schedule(List<Day> days) {
        this.days = days;
    }

    public List<Day> getDays() {
        return days;
    }

    public void setDays(List<Day> days) {
        this.days = days;
    }
}

class Employee {
    private double hourlyRate;
    private int regularHoursPerDay;
    private int weeklyOvertimeThreshold;
    private double overtimeRate1;
    private double overtimeRate2;
    private double overnightOvertimeRate;
    private double holidayOvertimeRate;

    public Employee() {
        // Default constructor
    }

    public double getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(double hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    public int getRegularHoursPerDay() {
        return regularHoursPerDay;
    }

    public void setRegularHoursPerDay(int regularHoursPerDay) {
        this.regularHoursPerDay = regularHoursPerDay;
    }

    public int getWeeklyOvertimeThreshold() {
        return weeklyOvertimeThreshold;
    }

    public void setWeeklyOvertimeThreshold(int weeklyOvertimeThreshold) {
        this.weeklyOvertimeThreshold = weeklyOvertimeThreshold;
    }

    public double getOvertimeRate1() {
        return overtimeRate1;
    }

    public void setOvertimeRate1(double overtimeRate1) {
        this.overtimeRate1 = overtimeRate1;
    }

    public double getOvertimeRate2() {
        return overtimeRate2;
    }

    public void setOvertimeRate2(double overtimeRate2) {
        this.overtimeRate2 = overtimeRate2;
    }

    public double getOvernightOvertimeRate() {
        return overnightOvertimeRate;
    }

    public void setOvernightOvertimeRate(double overnightOvertimeRate) {
        this.overnightOvertimeRate = overnightOvertimeRate;
    }

    public double getHolidayOvertimeRate() {
        return holidayOvertimeRate;
    }

    public void setHolidayOvertimeRate(double holidayOvertimeRate) {
        this.holidayOvertimeRate = holidayOvertimeRate;
    }
}

class PaycheckCalculator {
    private Employee employee;

    public PaycheckCalculator(Employee employee) {
        this.employee = employee;
    }

    public double calculatePaycheck(Schedule schedule) throws ParseException {
        double totalPay = 0;

        for (Day day : schedule.getDays()) {
            double totalRegularHours = calculateRegularHours(day);
            double totalOvertimeHours = calculateOvertimeHours(day);
            totalPay += calculateDailyPay(totalRegularHours, totalOvertimeHours);
        }

        return totalPay/3.6;
    }

    private double calculateRegularHours(Day day) throws ParseException {
        long minutesWorked = calculateMinutesWorked(day);
        return Math.min(minutesWorked, employee.getRegularHoursPerDay() * 60);
    }

    private double calculateOvertimeHours(Day day) throws ParseException {
        long minutesWorked = calculateMinutesWorked(day);
        long regularHoursInMinutes = employee.getRegularHoursPerDay() * 60;

        long overtimeMinutes = Math.max(0, minutesWorked - regularHoursInMinutes);

        return Math.min(overtimeMinutes, employee.getWeeklyOvertimeThreshold() * 60) +
                calculateNightOvertimeHours(day);
    }

    private double calculateNightOvertimeHours(Day day) throws ParseException {
        LocalDateTime timeIn = day.getFormattedTimeIn();
        LocalDateTime timeOut = day.getFormattedTimeOut();
        long nightOvertimeMinutes = 0;

        if (timeIn.getHour() >= 2 && timeOut.getHour() <= 5) {
            nightOvertimeMinutes = Duration.between(timeIn, timeOut).toMinutes();
        }

        return nightOvertimeMinutes;
    }

    private double calculateDailyPay(double totalRegularHours, double totalOvertimeHours) {
        double regularPay = totalRegularHours * employee.getHourlyRate();
        double overtimePay = totalOvertimeHours * calculateOvertimeRate();

        return regularPay + overtimePay;
    }

    private double calculateOvertimeRate() {
        return employee.getOvertimeRate1();
    }

    private long calculateMinutesWorked(Day day) throws ParseException {
        LocalDateTime timeIn = day.getFormattedTimeIn();
        LocalDateTime timeOut = day.getFormattedTimeOut();
        long minutesWorked = Duration.between(timeIn, timeOut).toMinutes();
        return  minutesWorked - calculateBreakTime(day);
    }

private long calculateBreakTime(Day day) throws ParseException {
    double breakInterval = 3.5; // Break after every 3.5 hours
    int breakDuration = 30; // Break duration in minutes
    long breakTime = 0;

    LocalDateTime timeIn = day.getFormattedTimeIn();
    LocalDateTime timeOut = day.getFormattedTimeOut();
    long minutesWorked = Duration.between(timeIn, timeOut).toMinutes();

    for (double i = breakInterval; i <= minutesWorked; i += breakInterval) {
        if (i <= minutesWorked) {
            breakTime += breakDuration;
        }
    }

    return breakTime;
}



    public static void main(String[] args) throws ParseException {
        Scanner scanner = new Scanner(System.in);

        // Get employee details
        Employee employee = new Employee();
        
        employee.setHourlyRate(1);
        employee.setRegularHoursPerDay(9);
        employee.setWeeklyOvertimeThreshold(50);
        employee.setOvertimeRate1(1.2);
        employee.setOvertimeRate2(1.35);
        employee.setOvernightOvertimeRate(1.25);
        employee.setHolidayOvertimeRate(1.30);
        PaycheckCalculator calculator = new PaycheckCalculator(employee);
        System.out.print("Enter The number of days you want to calculate : ");
        int numberOfDays = scanner.nextInt();

        List<Day> days = new ArrayList<>();
        System.out.print("Enter Date, Time In, and Time Out (e.g., 1-Aug-21 2:48 pm 4:24 pm): ");
        for (int i = 0; i < numberOfDays; i++) {
        
        String inputLine = scanner.nextLine();

        // Split the input line
        String[] inputParts = inputLine.split("\\s+");

                if (inputParts.length >= 3) {
                    String date = inputParts[0];
                    String timeIn = inputParts[1] + " " + inputParts[2]; 
                    String timeOut = inputParts.length >= 5 ? inputParts[3] + " " + inputParts[4] : "";

                    days.add(new Day(date, timeIn, timeOut));
                } else {
                    System.out.println("Invalid input format. Please enter in the correct format.");
                    i--; // Decrement i to repeat the current iteration
                }
        }

        Schedule schedule = new Schedule(days);

        // Calculate and print the paycheck
        double totalPay = calculator.calculatePaycheck(schedule);
        System.out.println("Total Earnings for the Nurse: ₹" + totalPay);

        scanner.close();
    }
}




package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class UserMealsUtil {
    public static void main(String[] args) {
        List<UserMeal> meals = Arrays.asList(
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
        );

        List<UserMealWithExcess> mealsTo = filteredByCycles(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealsTo.forEach(System.out::println);

        System.out.println(filteredByStreams(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000));
    }

    public static List<UserMealWithExcess> filteredByCycles(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        // TODO return filtered list with excess. Implement by cycles
        Map<LocalDate, Integer> mapCalories = new HashMap<>();
        for(UserMeal userMeal : meals){
            if(mapCalories.containsKey(userMeal.getDateTime().toLocalDate())){
                int calories = mapCalories.get(userMeal.getDateTime().toLocalDate()) + userMeal.getCalories();
                mapCalories.put(userMeal.getDateTime().toLocalDate(), calories);
            }
            else {
                mapCalories.put(userMeal.getDateTime().toLocalDate(), userMeal.getCalories());
            }
        }
        List<UserMealWithExcess> list = new ArrayList<>();
        for(UserMeal userMeal : meals){
            if(TimeUtil.isBetweenHalfOpen(userMeal.getDateTime().toLocalTime(), startTime, endTime)){
               if(mapCalories.get(userMeal.getDateTime().toLocalDate()) < caloriesPerDay){
                   list.add(createMeal(userMeal, true));
               } else{
                   list.add(createMeal(userMeal, false));
               }
            }
        }

        return list;
    }

    public static List<UserMealWithExcess> getFilterPredicate(List<UserMeal> meals, int caloriesPerDay, Predicate<UserMeal> filter){
        Map<LocalDate, Integer> map = meals.stream().collect(Collectors.groupingBy(userMeal -> userMeal.getDateTime().toLocalDate(), Collectors.summingInt(UserMeal::getCalories)));
        List<UserMealWithExcess> list = meals.stream().filter(filter)
                .map(userMeal -> createMeal(userMeal, map.get(userMeal.getDateTime().toLocalDate()) > caloriesPerDay))
                .collect(Collectors.toList());
        return list;
    }

    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        // TODO Implement by streams
        return getFilterPredicate(meals, caloriesPerDay, userMeal -> TimeUtil.isBetweenHalfOpen(userMeal.getDateTime().toLocalTime(), startTime, endTime));
    }

    public static UserMealWithExcess createMeal(UserMeal userMeal, boolean excess){
        return new UserMealWithExcess(userMeal.getDateTime(), userMeal.getDescription(), userMeal.getCalories(), excess);
    }
}

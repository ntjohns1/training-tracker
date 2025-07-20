package com.noslen.training_tracker.service.day;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.noslen.training_tracker.model.day.Day;
import com.noslen.training_tracker.repository.day.DayRepo;

@Service
public class DayServiceImpl implements DayService{

private final DayRepo repo;

    public DayServiceImpl(DayRepo repo) {
        this.repo = repo;
    }

    @Override
    public Day createDay(Day day) {
        day.setCreatedAt(Instant.now());
        day.setUpdatedAt(Instant.now());
        return repo.save(day);
    }

    @Override
    public Day updateDay(Long id, Day day) {
        Optional<Day> dayToUpdate = this.repo.findById(id);
        if (dayToUpdate.isPresent()) {
            dayToUpdate.get().setUpdatedAt(Instant.now());
            this.repo.save(dayToUpdate.get());
        }
        return dayToUpdate.orElse(null);
    }

    @Override
    public Day getDay(Long id) {
        Optional<Day> day = this.repo.findById(id);
        return day.orElse(null);
    }

    @Override
    public void deleteDay(Long id) {
        this.repo.deleteById(id);
    }

    @Override
    public List<Day> getDaysByMesocycleId(Long mesocycleId) {
        Optional<List<Day>> days = Optional.ofNullable(this.repo.findByMesocycleId(mesocycleId));
        return days.orElse(null);
    }

}

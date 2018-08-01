package io.pivotal.pal.tracker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.boot.actuate.metrics.GaugeService;

import java.sql.Time;
import java.util.List;


@RestController
public class TimeEntryController {

//    @Autowired
    private TimeEntryRepository timeEntryRepository;
    private final CounterService counter;
    private final GaugeService gauge;

    public TimeEntryController(
            TimeEntryRepository timeEntriesRepo,
            CounterService counter,
            GaugeService gauge
    ) {
        this.timeEntryRepository = timeEntriesRepo;
        this.counter = counter;
        this.gauge = gauge;
    }

    @PostMapping("/time-entries")
    public ResponseEntity create(@RequestBody TimeEntry timeEntry){


        TimeEntry entry = timeEntryRepository.create(timeEntry);
        if (entry != null) {
            System.out.println("WHAT IS timeENtry Repo : " + entry.getProjectId());
        }

        counter.increment("TimeEntry.created");
        gauge.submit("timeEntries.count", timeEntryRepository.list().size());
        return new ResponseEntity<>(entry, HttpStatus.CREATED);

    }

    @GetMapping("/time-entries/{id}")
    public ResponseEntity<TimeEntry> read(@PathVariable long id){
        System.out.println("id:" + id);
        TimeEntry timeEntryRepo = timeEntryRepository.find(id);
        if(timeEntryRepo == null) {
            counter.increment("TimeEntry.read");
            return new ResponseEntity<>(timeEntryRepo, HttpStatus.NOT_FOUND);
        }
        else {
            return new ResponseEntity<>(timeEntryRepo, HttpStatus.OK);
        }

    }

    @GetMapping("/time-entries")
    public ResponseEntity<List<TimeEntry>> list(){
        counter.increment("TimeEntry.listed");
        List<TimeEntry> listEntry  = timeEntryRepository.list();
        return new ResponseEntity<>(listEntry, HttpStatus.OK);

    }

    @PutMapping("/time-entries/{id}")
    public ResponseEntity update(@PathVariable long id, @RequestBody TimeEntry timeEntry)
    {
        TimeEntry timeEntryRepo = timeEntryRepository.update(id,timeEntry);
        if(timeEntryRepo == null)
        {
            counter.increment("TimeEntry.updated");
            return new ResponseEntity<>(timeEntryRepo, HttpStatus.NOT_FOUND);
        }
        else {
            return new ResponseEntity<>(timeEntryRepo, HttpStatus.OK);
        }

    }

    @DeleteMapping("/time-entries/{id}")
    public ResponseEntity<TimeEntry> delete(@PathVariable long id){
        timeEntryRepository.delete(id);
        counter.increment("TimeEntry.deleted");
        gauge.submit("timeEntries.count", timeEntryRepository.list().size());
        return new ResponseEntity<TimeEntry>(HttpStatus.NO_CONTENT);
    }

}

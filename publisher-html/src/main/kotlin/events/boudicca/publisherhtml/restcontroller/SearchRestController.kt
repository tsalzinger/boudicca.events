package events.boudicca.publisherhtml.restcontroller

import events.boudicca.api.Event
import events.boudicca.openapi.model.SearchDTO
import events.boudicca.publisherhtml.service.EventService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@RestController
@RequestMapping("/api")
class SearchRestController @Autowired constructor(private val eventService: EventService) {

    @GetMapping("/search")
    @ResponseBody
    fun search(@RequestParam("name") name: String,
               @RequestParam("fromDate") fromDate: String,
               @RequestParam("toDate") toDate: String): ResponseEntity<Set<Event>> {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        // TODO: useragent dependend timezone
        val fromDateParsed = LocalDate.parse(fromDate, formatter)
                .atTime(0 ,0,0)
                .atZone(ZoneId.systemDefault()).toOffsetDateTime();
        val toDateParsed = LocalDate.parse(toDate, formatter)
                .atTime(0 ,0,0)
                .atZone(ZoneId.systemDefault()).toOffsetDateTime();

        val events = eventService.search(SearchDTO()
                .name(name)
                .fromDate(fromDateParsed)
                .toDate(toDateParsed))
        return ResponseEntity(events, HttpStatus.OK)
    }
}






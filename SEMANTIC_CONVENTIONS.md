# Semantic Conventions

This document explains which properties Boudicca understands, what their meaning is, and what format the values should
be
in. Events can have other properties which will be searchable as text, but will not get special treatment from
Boudicca.

### General encodings

For Boudicca each event is a collection of key-value pairs, for some of those we have defined a special meaning.
Normally when you talk with Boudicca you do that via our REST-API, where each event is encoded into a JSON-object. The
whole JSON-document should be encoded in UTF-8.

### Data types

We use certain data types for the properties we expect.

* `text`: just a simple text/string
* `date`: [ISO 8601](https://en.wikipedia.org/wiki/ISO_8601) Timestamp as text, for example: `2009-06-30T18:30:00+02:00`
* `url`: A URL as text
* `coordinates`: longitude + latitude in Decimal degrees (DD) in the format `<latitude>, <longitute>`
* `list<?>`: A list of elements, the `?` describes the type of the elements in the list. Currently, elements in a list
  are seperated by a newline, but this will probably change sometime
* `enum<?>`: Has to be one of the specified distinct values
* `boolean`: The text "true" or "false"

### General Properties

**Events have only two required properties: `name` and `startDate`**

| Key           | Meaning                                                                                              | Format                               |
|---------------|------------------------------------------------------------------------------------------------------|--------------------------------------|
| name          | The name of the event                                                                                | text                                 |
| startDate     | Time of start for the event                                                                          | date                                 |
| endDate       | Time of end for the event                                                                            | date                                 |
| url           | A link to the website for this event                                                                 | url                                  |
| type          | The type of event, for example `concert`, `????` more examples please                                | text ??? maybe enum would be better? |
| category      | The category of an event, for example `MUSIC`, `ART` or `TECH`, see EventCategory enum               | enum\<EventCategory>                 |
| description   | Text describing this event                                                                           | text                                 |
| tags          | A list of tags. TODO how to describe?                                                                | list\<text>                          |
| registration  | If this is a free event, a event which requires registration or a event which requires a paid ticket | enum\<registration>                  |
| pictureUrl    | Url to a picture to be shown                                                                         | url                                  |
| collectorName | Name of the collector which collected this event                                                     | text                                 |
| sources       | A list of all sources, line by line. This should include all URLs or other sources used.             | list\<text>                          |

#### Registration enum values

* `free`: a free event which neither requires registration nor a ticket
* `registration`: an event which requires a free registration
* `ticket`: a paid event which requires a ticket

#### Category enum values

* `MUSIC`: concerts or other events where the main focus is music
* `TECH`: event with technology as the focus
* `ART`: art exhibitions, comedy, theater, ...

### Location Properties

| Key                  | Meaning                                       | Format      |
|----------------------|-----------------------------------------------|-------------|
| location.name        | The name of the location the event is held in | text        |
| location.url         | A link to the website of the location         | url         |
| location.coordinates | Map-coordinates for the location              | coordinates |
| location.city        | The city of the event                         | text        |
| location.address     | Full address line for the location            | text        |

### Accessibility Properties

| Key                                   | Meaning                                                    | Format  |
|---------------------------------------|------------------------------------------------------------|---------|
| accessibility.accessibleEntry         | If the entry/exit is accessible                            | boolean |
| accessibility.accessibleSeats         | If there are wheelchair places available on the event hall | boolean |
| accessibility.accessibleToilets       | If there are accessible toilets available                  | boolean |
| accessibility.accessibleAktivpassLinz | If the Aktivpass Linz may be applicable                    | boolean |
| accessibility.accessibleKulturpass    | If the Kulturpass may be applicable                        | boolean |

### Concert(/Music?) Properties

| Key              | Meaning                   | Format     |
|------------------|---------------------------|------------|
| concert.genre    | Genre of the concert      | text       |
| concert.bandlist | List of all bands playing | list<text> |

### Internal Properties

The property name prefix `internal.*` is reserved by Boudicca for internal properties needed and will be silently
discarded if sent by an EventCollector.
package pt.unl.fct.iadi.novaevents.service

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import pt.unl.fct.iadi.novaevents.model.*
import pt.unl.fct.iadi.novaevents.repository.ClubRepository
import pt.unl.fct.iadi.novaevents.repository.EventRepository
import pt.unl.fct.iadi.novaevents.repository.EventTypeRepository
import java.time.LocalDate
import java.util.*

class TestEventService {

    private val eventRepo = mock<EventRepository>()
    private val clubRepo = mock<ClubRepository>()
    private val typeRepo = mock<EventTypeRepository>()
    private val service = EventService(clubRepo, eventRepo, typeRepo)

    @Test
    fun `create saves new event`() {
        val club = Club(id = 1, name = "Hiking & Outdoors Club")
        val type = EventType(id = 1, name = "SOCIAL")
        val owner = User(id = 1, username = "alice", password = "x")

        whenever(eventRepo.existsByNameIgnoreCase(any())).thenReturn(false)
        whenever(clubRepo.findById(1)).thenReturn(Optional.of(club))
        whenever(typeRepo.findById(1)).thenReturn(Optional.of(type))
        whenever(eventRepo.save(any<Event>())).thenAnswer { it.arguments[0] }

        val ev = service.create(1, "Trail", LocalDate.now(), "Sintra", 1, "desc", owner)

        assertEquals("Trail", ev.name)
        verify(eventRepo).save(any<Event>())
    }

    @Test
    fun `create throws on duplicate name`() {
        whenever(eventRepo.existsByNameIgnoreCase(any())).thenReturn(true)
        val owner = User(id = 1, username = "alice", password = "x")

        assertThrows(IllegalArgumentException::class.java) {
            service.create(1, "Dup", LocalDate.now(), "Loc", 1, null, owner)
        }
    }

    @Test
    fun `getById throws when not found`() {
        whenever(eventRepo.findById(99)).thenReturn(Optional.empty())
        assertThrows(NoSuchElementException::class.java) { service.getById(99) }
    }
}
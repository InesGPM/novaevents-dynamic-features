package pt.unl.fct.iadi.novaevents.service

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import pt.unl.fct.iadi.novaevents.model.Club
import pt.unl.fct.iadi.novaevents.model.Event
import pt.unl.fct.iadi.novaevents.model.EventType
import pt.unl.fct.iadi.novaevents.model.User
import pt.unl.fct.iadi.novaevents.repository.ClubRepository
import pt.unl.fct.iadi.novaevents.repository.EventRepository
import pt.unl.fct.iadi.novaevents.repository.EventTypeRepository
import java.time.LocalDate
import java.util.Optional

class TestEventService {

    private val eventRepo: EventRepository = mock(EventRepository::class.java)
    private val clubRepo: ClubRepository = mock(ClubRepository::class.java)
    private val typeRepo: EventTypeRepository = mock(EventTypeRepository::class.java)
    private val service = EventService(clubRepo, eventRepo, typeRepo)

    @Test
    fun `create saves new event`() {
        val club = Club(id = 1, name = "Hiking & Outdoors Club")
        val type = EventType(id = 1, name = "SOCIAL")
        val owner = User(id = 1, username = "alice", password = "x")

        `when`(eventRepo.existsByNameIgnoreCase("Trail")).thenReturn(false)
        `when`(clubRepo.findById(1L)).thenReturn(Optional.of(club))
        `when`(typeRepo.findById(1L)).thenReturn(Optional.of(type))
        `when`(eventRepo.save(ArgumentMatchers.any(Event::class.java)))
            .thenAnswer { it.arguments[0] }

        val ev = service.create(1, "Trail", LocalDate.now(), "Sintra", 1, "desc", owner)

        assertEquals("Trail", ev.name)
        assertEquals("Sintra", ev.location)
        verify(eventRepo).save(ArgumentMatchers.any(Event::class.java))
    }

    @Test
    fun `create throws on duplicate name`() {
        `when`(eventRepo.existsByNameIgnoreCase("Dup")).thenReturn(true)
        val owner = User(id = 1, username = "alice", password = "x")

        assertThrows(IllegalArgumentException::class.java) {
            service.create(1, "Dup", LocalDate.now(), "Loc", 1, null, owner)
        }
    }

    @Test
    fun `create throws when club missing`() {
        val owner = User(id = 1, username = "alice", password = "x")
        `when`(eventRepo.existsByNameIgnoreCase("X")).thenReturn(false)
        `when`(clubRepo.findById(99L)).thenReturn(Optional.empty())

        assertThrows(NoSuchElementException::class.java) {
            service.create(99, "X", LocalDate.now(), "Loc", 1, null, owner)
        }
    }

    @Test
    fun `getById throws when not found`() {
        `when`(eventRepo.findById(99L)).thenReturn(Optional.empty())
        assertThrows(NoSuchElementException::class.java) { service.getById(99L) }
    }

    @Test
    fun `getAll delegates to repository`() {
        `when`(eventRepo.findAllWithClubAndType()).thenReturn(emptyList())
        assertTrue(service.getAll().isEmpty())
    }

    @Test
    fun `getAllTypes delegates to repository`() {
        `when`(typeRepo.findAll()).thenReturn(emptyList())
        assertTrue(service.getAllTypes().isEmpty())
    }

    @Test
    fun `delete throws when not found`() {
        `when`(eventRepo.findById(42L)).thenReturn(Optional.empty())
        assertThrows(NoSuchElementException::class.java) { service.delete(42L) }
    }
}

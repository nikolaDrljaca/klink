import com.drbrosdev.klinkrest.application.KlinkApplicationServiceImpl
import com.drbrosdev.klinkrest.application.KlinkApplicationServiceMapper
import com.drbrosdev.klinkrest.domain.KlinkDomainService
import com.drbrosdev.klinkrest.domain.dto.KlinkDto
import com.drbrosdev.klinkrest.domain.dto.KlinkEntryDto
import org.mapstruct.factory.Mappers
import spock.lang.Specification

import static java.time.LocalDateTime.now
import static java.util.UUID.fromString

class KlinkApplicationServiceImplTest extends Specification {

    private final KlinkDomainService klinkDomainService = Mock(KlinkDomainService)

    private final KlinkApplicationServiceMapper mapper = Mappers.getMapper(KlinkApplicationServiceMapper)

    private final KlinkApplicationServiceImpl service = new KlinkApplicationServiceImpl(
            klinkDomainService,
            mapper)

    def "test executeKlinkCleanup no eligible klinks found"() {
        when:
        service.executeKlinkCleanup()

        then:
        1 * klinkDomainService.getKlinks() >> {
            [].stream()
        }
        0 * klinkDomainService.deleteKlinksIn(_)
    }

    def "test executeKlinkCleanup found eligible klinks"() {
        given:
        service.daysToKeepKlinks = 30

        when:
        service.executeKlinkCleanup()

        then:
        1 * klinkDomainService.getKlinks() >> {
            [
                    // no entries, updated at in minus 30 days - eligible
                    KlinkDto.builder()
                            .id(fromString("07fdfe4b-340e-46e7-90e8-5c27b0c7c946"))
                            .updatedAt(now().minusDays(31))
                            .entries([])
                            .build(),
                    // no entries, updated at a few days ago
                    KlinkDto.builder()
                    .id(fromString("820cc663-134a-4aa3-b27a-662b6ab04565"))
                            .updatedAt(now().minusDays(7))
                            .entries([])
                            .build(),
                    // entries, updated at in minus 30 days - not eligible
                    KlinkDto.builder()
                    .id(fromString("6539a72f-34c2-4933-a594-d6ec9f3ea277"))
                            .updatedAt(now().minusDays(31))
                            .entries([
                                    KlinkEntryDto.builder()
                                            .createdAt(now())
                                            .build()
                            ])
                            .build(),
                    // entries, updated at in minus 30 days - eligible
                    KlinkDto.builder()
                    .id(fromString("fd81d1dd-df13-4a0a-b373-a3b130032297"))
                            .updatedAt(now().minusDays(31))
                            .entries([
                                    KlinkEntryDto.builder()
                                            .createdAt(now().minusDays(34))
                                            .build()
                            ])
                            .build(),
            ].stream()
        }
        1 * klinkDomainService.deleteKlinksIn([
                fromString("07fdfe4b-340e-46e7-90e8-5c27b0c7c946"),
                fromString("fd81d1dd-df13-4a0a-b373-a3b130032297"),
        ])

        cleanup:
        service.daysToKeepKlinks = 0
    }
}

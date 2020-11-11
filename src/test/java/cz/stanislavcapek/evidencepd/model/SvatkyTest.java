package cz.stanislavcapek.evidencepd.model;

import cz.stanislavcapek.evidencepd.svatky.Svatky;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Log4j2
class SvatkyTest {

    @Test
    void ziskejInstanciSVatku() {
        final Svatky instance = Svatky.getInstance(2020);

        log.info(instance.toString());
        assertAll(() -> {
            assertEquals(2020, instance.getRok());
            assertNotEquals(0, instance.getDatumySvatku().size());
        });
    }
}
package cz.stanislavcapek.evidencepd.zamestnanec;

import cz.stanislavcapek.evidencepd.dao.Dao;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


class ZamestnanciDaoTest {

    private static final List<Zamestnanec> zamestnanci = new ArrayList<>();
    private final Path testDir = Paths.get("src/test/resources/zamestnanci");

    @BeforeAll
    static void beforeAll() {
        final Zamestnanec z1 = new Zamestnanec(1, "Petr", "Novák");
        final Zamestnanec z2 = new Zamestnanec(2, "Ondra", "Malý");
        final Zamestnanec z3 = new Zamestnanec(3, "Markéta", "Pospíšilová");
        final Zamestnanec z4 = new Zamestnanec(4, "Lenka", "Drahá");
        zamestnanci.add(z1);
        zamestnanci.add(z2);
        zamestnanci.add(z3);
        zamestnanci.add(z4);
    }

    @Test
    void ulozeniSeznamuZamestnancu() {
        final Dao<List<Zamestnanec>> dao = new ZamestnanciDao();
        final Path cesta = Paths.get(testDir.toString(), "ulozeniZamestnanci.json");

        try {
            dao.uloz(cesta, zamestnanci);
            assert true;
        } catch (IOException e) {
            e.printStackTrace();
            assert false;
        }
    }

    @Test
    void nacteniSeznamuZamestnancu() {
        final Dao<List<Zamestnanec>> dao = new ZamestnanciDao();
        final Path cesta = Paths.get(testDir.toString(), "nacteniZamestnanci.json");

        try {
            final List<Zamestnanec> zamestnanecList = dao.nacti(cesta);
            Assertions.assertEquals(4, zamestnanecList.size());
            assert true;
        } catch (IOException e) {
            e.printStackTrace();
            assert false;
        }
    }
}
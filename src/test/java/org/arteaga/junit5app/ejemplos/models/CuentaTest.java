package org.arteaga.junit5app.ejemplos.models;

import org.arteaga.junit5app.ejemplos.exceptions.DineroInsuficienteException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CuentaTest {
    Cuenta cuenta;

    private TestInfo testInfo;
    private TestReporter testReporter;

    @BeforeEach
    void initMethodTest(TestInfo testInfo, TestReporter testReporter) {
        this.cuenta = new Cuenta("Andres", new BigDecimal(1000.12345));
        this.testInfo = testInfo;
        this.testReporter = testReporter;
        System.out.println("Iniciando el metodo.");
        //System.out.println("Ejecutando: " + testInfo.getDisplayName() + " " + testInfo.getTestMethod().orElse(null).getName()
        //  + " con las etiquetas " + testInfo.getTags());
        testReporter.publishEntry("Ejecutando: " + testInfo.getDisplayName() + " " + testInfo.getTestMethod().orElse(null).getName()
          + " con las etiquetas " + testInfo.getTags());
    }

    @AfterEach
    void tearDown() {
        System.out.println("finalizando el metodo de prueba.");
    }

    @BeforeAll
    static void beforeAll() {
        System.out.println("Inicializando el Test");
    }

    @AfterAll
    static void afterAll() {
        System.out.println("Finalizando el Test");
    }

    @Tag("cuenta")
    @Nested
    @DisplayName("probando atributos de la cuenta")
    class cuentaTestNombreSaldo{
        @Test
        @DisplayName("Probando nombre de la cuenta corriente!")
        void testNombreCuenta() {
            testReporter.publishEntry(testInfo.getTags().toString());
            if(testInfo.getTags().contains("cuenta")){
                testReporter.publishEntry("hacer algo con la etiqueta cuenta");
            }
            //cuenta.setPersona("Andres");
            String esperado = "Andres";
            String real = cuenta.getPersona();
            assertNotNull(real, () -> "La cuenta no puede ser nula");
            assertEquals(esperado, real, () -> "El nombre de la cuenta no es el que se esperaba " + esperado
              + " sin embargo fue " + real);
            assertTrue(real.equals("Andres"), () -> "Nombre cuenta esperada debe ser igual a la real");
        }

        @Test
        @DisplayName("el saldo de la cuenta corriente, que no sea null, mayor que cero, valor esperado")
        void testSaldoCuenta() {
            assertNotNull(cuenta.getSaldo());
            assertEquals(1000.12345, cuenta.getSaldo().doubleValue());
            assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }

        @Test
        @DisplayName("testeando referencias que sean iguales con el metodo equals.")
        void testReferenciaCuenta() {
            Cuenta cuenta1 = new Cuenta("John Doe", new BigDecimal("8900.9997"));
            Cuenta cuenta2 = new Cuenta("John Doe", new BigDecimal("8900.9997"));

            //assertNotEquals(cuenta2, cuenta1);
            assertEquals(cuenta2, cuenta1);
        }
    }
    @Nested
    @DisplayName("probando operaciones cuentas")
    class CuentOperacionesTest{
        @Tag("cuenta")
        @Test
        @DisplayName("testeando realizar un debito de una cuenta corriente")
        void testDebitoCuenta() {
            cuenta.debito(new BigDecimal(100));
            assertNotNull(cuenta.getSaldo());
            assertEquals(900, cuenta.getSaldo().intValue());
            assertEquals("900.12345", cuenta.getSaldo().toPlainString());
        }
        @Tag("cuenta")
        @Test
        void testCreditoCuenta() {
            cuenta.credito(new BigDecimal(100));
            assertNotNull(cuenta.getSaldo());
            assertEquals(1100, cuenta.getSaldo().intValue());
            assertEquals("1100.12345", cuenta.getSaldo().toPlainString());
        }

        @Tag("cuenta")
        @Tag("banco")
        @Test
        void testTransferirDineroCuentas() {
            Cuenta cuenta1 = new Cuenta("Jhon Doe", new BigDecimal("2500"));
            Cuenta cuenta2 = new Cuenta("Andres", new BigDecimal("1500.89890"));

            Banco banco = new Banco();
            banco.setNombre("Banco del Estado");
            banco.transferir(cuenta2, cuenta1, new BigDecimal(500));
            assertEquals("1000.89890", cuenta2.getSaldo().toPlainString());
            assertEquals("3000.00000", cuenta1.getSaldo().toPlainString());
        }
    }

    @Test
    @Tag("cuenta") //agrupar nuestras pruebas por categorias.
    @Tag("error")
    void testDineroInsuficienteExceptionCuenta() {
        Exception exception = assertThrows(DineroInsuficienteException.class, () -> {
            cuenta.debito(new BigDecimal("1500"));
        });

        String actual = exception.getMessage();
        String esperado = "Dinero Insuficiente";
        assertEquals(esperado, actual);
    }

    @Test
    @Tag("cuenta")
    @Tag("banco")
    void testRelacionBancoCuentas() {
        Cuenta cuenta1 = new Cuenta("Jhon Doe", new BigDecimal("2500"));
        Cuenta cuenta2 = new Cuenta("Andres", new BigDecimal("1500.89890"));

        Banco banco = new Banco();
        banco.addCuenta(cuenta1);
        banco.addCuenta(cuenta2);
        banco.setNombre("Banco del Estado");
        banco.transferir(cuenta2, cuenta1, new BigDecimal(500));
        assertEquals("1000.89890", cuenta2.getSaldo().toPlainString());
        assertEquals("3000.00000", cuenta1.getSaldo().toPlainString());

        assertEquals(2, banco.getCuentas().size());
        assertEquals("Banco del Estado", cuenta1.getBanco().getNombre());
        assertEquals("Andres", banco.getCuentas().stream()
                .filter(c -> c.getPersona().equals("Andres"))
                .findFirst().get().getPersona()
        );
    }

    @Test
    @Disabled
    @DisplayName("probando relaciones entre las cuentas y el banco con assertAll")
    void testRelacionBancoCuentas2() {
        fail(); //metodo estatico que hace que falle el test
        Cuenta cuenta1 = new Cuenta("Jhon Doe", new BigDecimal("2500"));
        Cuenta cuenta2 = new Cuenta("Andres", new BigDecimal("1500.8989"));

        Banco banco = new Banco();
        banco.addCuenta(cuenta1);
        banco.addCuenta(cuenta2);
        banco.setNombre("Banco del Estado");
        banco.transferir(cuenta2, cuenta1, new BigDecimal(500));
        assertAll(
                () -> assertEquals("1000.8989", cuenta2.getSaldo().toPlainString(), () ->
                        "El valor del saldo de la cuenta2 no es el esperado"),
                () -> assertEquals("3000", cuenta1.getSaldo().toPlainString(), () ->
                        "El valor del saldo de la cuenta1 no es el esperado"),
                () -> assertEquals(2, banco.getCuentas().size(), () ->
                        "el banco no tienes las cuentas esperadas"),
                () -> assertEquals("Banco del Estado", cuenta1.getBanco().getNombre()),
                () -> assertTrue(banco.getCuentas().stream()
                        .filter(c -> c.getPersona().equals("Andres"))
                        .findFirst().isPresent()),
                () -> assertTrue(banco.getCuentas().stream()
                        .anyMatch(c -> c.getPersona().equals("Andres"))));
    }

    @Nested
    class SistemaOperativoTest{
        @Test
        @EnabledOnOs(OS.WINDOWS)
        void testSoloWindows() {

        }

        @Test
        @EnabledOnOs({OS.LINUX, OS.MAC})
        void testSoloLinuxMac() {

        }

        @Test
        @DisabledOnOs(OS.WINDOWS)
        void testNoWindows() {
        }
    }
    @Nested
    class JavaVersionTest{
        @Test
        @EnabledOnJre(JRE.JAVA_8)
        void soloJdk8() {
        }

        @Test
        @EnabledOnJre(JRE.JAVA_11)
        void soloJdk11() {
        }

        @Test
        @DisabledOnJre(JRE.JAVA_11)
        void testNoJdk11() {
        }
    }
    @Nested
    class SystemPropertiesTest{
        @Test
        void imprimirSystemProperties() {
            Properties properties = System.getProperties();
            properties.forEach((k, v) -> System.out.println(k + ":" + v));
        }

        @Test
        @EnabledIfSystemProperty(named = "java.version", matches = "11.0.3")
        void testJavaVersion() {
        }

        @Test
        @DisabledIfSystemProperty(named = "os.arch", matches = ".*32.*")
        void testSolo64() {
        }

        @Test
        @EnabledIfSystemProperty(named = "os.arch", matches = ".*32.*")
        void testNo64() {
        }

        @Test
        @EnabledIfSystemProperty(named = "user.name", matches = "aarteagq")
        void testUserName() {
        }

        @Test
        @EnabledIfSystemProperty(named = "ENV", matches = "dev")
        void testDev() {
        }
    }
    @Nested
    class VariableAmbienteTest{
        @Test
        void imprimirVariablesAmbiente() {
            Map<String, String> getEnv = System.getenv();
            getEnv.forEach((k, v) -> System.out.println(k + "=" + v));
        }

        @Test
        @EnabledIfEnvironmentVariable(named = "JAVA_HOME", matches = ".*jdk-11.0.3.*")
        void testJavaHome(){

        }

        @Test
        @EnabledIfEnvironmentVariable(named = "NUMBER_OF_PROCESSORS", matches = "12")
        void testProcesadores(){
        }

        @Test
        @EnabledIfEnvironmentVariable(named = "ENVIRONMENT", matches = "dev")
        void testEnv() {
        }

        @Test
        @DisabledIfEnvironmentVariable(named = "ENVIRONMENT", matches = "prod")
        void testEvnProdDisabled() {
        }
    }

    @Test
    @DisplayName("testSaldoCuentaDev")
    void testSaldoCuentaDev() {
        boolean esDev = "dev".equals(System.getProperty("ENV"));
        assumeTrue(esDev); //si se cumple la suposion entonces el resto de assert se ejecutan, caso contrario deshabilita el test.
        assertNotNull(cuenta.getSaldo());
        assertEquals(1000.12345, cuenta.getSaldo().doubleValue());
        assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    @DisplayName("test Saldo Cuenta Dev 2")
    void testSaldoCuentaDev2() {
        boolean esDev = "dev".equals(System.getProperty("ENV"));
        assumingThat(esDev, () -> { //lo de adentro se ejecuta, dependiendo de la variable esDev.
            assertNotNull(cuenta.getSaldo());
            assertEquals(1000.12345, cuenta.getSaldo().doubleValue());
        });
        assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
    }

    @RepeatedTest(value=5, name = "{displayName} - Repeticion numero {currentRepetition} de {totalRepetitions}")
    @DisplayName("Test Repeated")
    void testCreditoCuentaRepeated(RepetitionInfo info) {
        if(info.getCurrentRepetition() == 3){
            System.out.println("estamos en la repeticion " + info.getCurrentRepetition());
        }
        cuenta.credito(new BigDecimal(100));
        assertNotNull(cuenta.getSaldo());
        assertEquals(1100, cuenta.getSaldo().intValue());
        assertEquals("1100.12345", cuenta.getSaldo().toPlainString());
    }
    @Tag("param")
    @Nested
    class PruebasParametrizadasTest{
        @ParameterizedTest(name = "numero {index} ejecutando con valor {0} - {argumentsWithNames}")
        @ValueSource(doubles = {100, 200, 300, 500, 1000.12345})
        @DisplayName("Test debito cuenta parameterized Test")
        void testDebitoCuentaValueSource(double monto) {
            cuenta.debito(new BigDecimal(monto));
            assertNotNull(cuenta.getSaldo());
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO)>0);
        }

        @ParameterizedTest(name = "numero {index} ejecutando con valor {0} - {argumentsWithNames}")
        @CsvSource({"1,100", "2,200", "3,300", "4,500", "5,700", "6,1000"})
        @DisplayName("Test debito cuenta using csv source Test")
        void testDebitoCuentaCsvSource(String index, double monto) {
            System.out.println(index + " -> " + monto);
            cuenta.debito(new BigDecimal(monto));
            assertNotNull(cuenta.getSaldo());
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO)>0);
        }

        @ParameterizedTest(name = "numero {index} ejecutando con valor {0} - {argumentsWithNames}")
        @CsvFileSource(resources = "/data.csv")
        @DisplayName("Test debito cuenta using csv file source Test")
        void testDebitoCuentaCsvFileSource(double monto) {
            cuenta.debito(new BigDecimal(monto));
            assertNotNull(cuenta.getSaldo());
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO)>0);
        }

        @ParameterizedTest(name = "numero {index} ejecutando con valor {0} - {argumentsWithNames}")
        @MethodSource("montoList")
        @DisplayName("Test debito cuenta using method Source Test")
        void testDebitoCuentaMethodSource(double monto) {
            cuenta.debito(new BigDecimal(monto));
            assertNotNull(cuenta.getSaldo());
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO)>0);
        }

        @ParameterizedTest(name = "numero {index} ejecutando con valor {0} - {argumentsWithNames}")
        @CsvSource({"200,100,John,Andres", "250,200,Pepe,Pepe", "300,300,maria,Maria", "510,500,Pepa,Pepa", "750,700,Lucas,Lucas", "1000.12345,1000.12345,Ana,Ana"})
        @DisplayName("Test debito cuenta using csv source UPDATE saldo Test")
        void testDebitoCuentaCsvSource2(String saldo, double monto, String esperado, String actual) {
            System.out.println(saldo + " -> " + monto);
            cuenta.setSaldo(new BigDecimal(saldo));
            cuenta.debito(new BigDecimal(monto));
            cuenta.setPersona(actual);

            assertNotNull(cuenta.getSaldo());
            assertNotNull(cuenta.getPersona());
            assertEquals(esperado, actual);
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO)>0);
        }

        @ParameterizedTest(name = "numero {index} ejecutando con valor {0} - {argumentsWithNames}")
        @CsvFileSource(resources = "/data2.csv")
        @DisplayName("Test debito cuenta using csv file source 2 Test")
        void testDebitoCuentaCsvFileSource2(String saldo, String monto, String esperado, String actual) {
            System.out.println(saldo + " -> " + monto);
            cuenta.setSaldo(new BigDecimal(saldo));
            cuenta.debito(new BigDecimal(monto));
            cuenta.setPersona(actual);

            assertNotNull(cuenta.getSaldo());
            assertNotNull(cuenta.getPersona());
            assertEquals(esperado, actual);
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO)>0);
        }
    }

    static List<String> montoList(){
        return Arrays.asList("100", "150", "300", "500", "1000.12345");
    }

    @Nested
    @Tag("timeout")
    class EjemploTimeoutTest{
        @Test
        @Timeout(1)
        void pruebaTimeout() throws  InterruptedException{
            TimeUnit.MILLISECONDS.sleep(100);
        }
        @Test
        @Timeout(value = 1000, unit = TimeUnit.MILLISECONDS)
        void pruebaTimeout2() throws  InterruptedException{
            TimeUnit.MILLISECONDS.sleep(1000);
        }

        @Test
        void testTimeoutAssertions(){
            assertTimeout(Duration.ofSeconds(5), () -> {
                TimeUnit.MILLISECONDS.sleep(4500);
            });
        }
    }
}
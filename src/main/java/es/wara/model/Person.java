package es.wara.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Modelo de entidad para una persona en el sistema.
 * <p>
 * Contiene información básica como nombre, apellido y fecha de nacimiento.
 * El identificador es único y generado automáticamente. Los setters validan los datos,
 * corrigiendo o ignorando valores inválidos y registrando advertencias mediante el logger.
 * </p>
 *
 * <h2>Características principales:</h2>
 * <ul>
 *   <li>ID único autogenerado y seguro para concurrencia</li>
 *   <li>Validación automática en setters</li>
 *   <li>Métodos de validación para reglas de negocio</li>
 * </ul>
 *
 * @author Wara
 * @version 1.2
 * @since 2025-09-25
 */
public class Person {

    /** Logger para registrar eventos y advertencias de la clase Person. */
    private static final Logger logger = LoggerFactory.getLogger(Person.class);

    /** Generador atómico de IDs únicos para cada persona. */
    private static AtomicInteger personSequence = new AtomicInteger(0);

    /** Identificador único de la persona. */
    private int personId;

    /** Nombre de la persona. */
    private String firstName;

    /** Apellido de la persona. */
    private String lastName;

    /** Fecha de nacimiento de la persona. */
    private LocalDate birthDate;

    /**
     * Constructor por defecto que crea una persona sin datos iniciales.
     * Los valores se establecen como {@code null} y el ID se genera automáticamente.
     */
    public Person() {
        this(null, null, null);
    }

    /**
     * Constructor parametrizado que crea una persona con los datos proporcionados.
     * Los datos se validan y corrigen automáticamente si no cumplen las reglas.
     *
     * @param firstName Nombre de la persona
     * @param lastName Apellido de la persona
     * @param birthDate Fecha de nacimiento de la persona
     */
    public Person(String firstName, String lastName, LocalDate birthDate) {
        this.personId = personSequence.incrementAndGet();
        setFirstName(firstName);
        setLastName(lastName);
        setBirthDate(birthDate);
    }

    /**
     * Devuelve el identificador único de la persona.
     *
     * @return ID único generado automáticamente
     */
    public int getPersonId() {
        return personId;
    }

    /**
     * Permite establecer el ID de la persona.
     * <b>Nota:</b> No se recomienda modificar el ID una vez asignado.
     *
     * @param personId Nuevo ID (debe ser positivo)
     */
    public void setPersonId(int personId) {
        if (personId > 0) {
            this.personId = personId;
        } else {
            logger.warn("ID no válido: {}", personId);
        }
    }

    /**
     * Devuelve el nombre de la persona.
     *
     * @return Nombre actual
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Establece el nombre de la persona.
     * Si el nombre es nulo o vacío, no se asigna.
     * Solo genera advertencia si se intenta asignar explícitamente un valor inválido
     * a un objeto que ya tenía un nombre válido.
     *
     * @param firstName Nombre a asignar
     */
    public void setFirstName(String firstName) {
        if (firstName != null && !firstName.trim().isEmpty()) {
            this.firstName = firstName.trim();
        } else if (this.firstName != null) {
            // Solo advertir si ya tenía un nombre válido y se intenta poner null/vacío
            logger.warn("Intento de asignar nombre vacío o nulo a persona existente. No se asigna.");
        }
        // Si this.firstName es null (inicialización), simplemente no asigna nada sin warning
    }

    /**
     * Devuelve el apellido de la persona.
     *
     * @return Apellido actual
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Establece el apellido de la persona.
     * Si el apellido es nulo o vacío, no se asigna.
     * Solo genera advertencia si se intenta asignar explícitamente un valor inválido
     * a un objeto que ya tenía un apellido válido.
     *
     * @param lastName Apellido a asignar
     */
    public void setLastName(String lastName) {
        if (lastName != null && !lastName.trim().isEmpty()) {
            this.lastName = lastName.trim();
        } else if (this.lastName != null) {
            // Solo advertir si ya tenía un apellido válido y se intenta poner null/vacío
            logger.warn("Intento de asignar apellido vacío o nulo a persona existente. No se asigna.");
        }
        // Si this.lastName es null (inicialización), simplemente no asigna nada sin warning
    }

    /**
     * Devuelve la fecha de nacimiento de la persona.
     *
     * @return Fecha de nacimiento actual
     */
    public LocalDate getBirthDate() {
        return birthDate;
    }

    /**
     * Establece la fecha de nacimiento de la persona.
     * Si la fecha es futura, no se asigna y se registra advertencia.
     *
     * @param birthDate Fecha a asignar
     */
    public void setBirthDate(LocalDate birthDate) {
        List<String> errores = new ArrayList<>();
        if (isValidBirthDate(birthDate)) {
            this.birthDate = birthDate;
        }
    }

    /**
     * Valida si una fecha de nacimiento es válida según las reglas (no puede ser futura).
     * Agrega mensajes de error a la lista proporcionada si la fecha no es válida.
     *
     * @param bdate Fecha de nacimiento a validar
     * @return {@code true} si la fecha es válida, {@code false} en caso contrario
     */
    public static boolean isValidBirthDate(LocalDate bdate) {
        if (bdate == null) {
            return true;
        }
        return !bdate.isAfter(LocalDate.now());
    }

    /**
     * Valida si la persona es válida: nombre y apellido no vacíos, fecha válida.
     * Agrega mensajes de error a la lista proporcionada si hay datos incorrectos.
     *
     * @return {@code true} si todos los datos son válidos, {@code false} si hay errores
     */
    public boolean isValidPerson() {
        boolean isValid = true;
        if (firstName == null || firstName.trim().isEmpty()) {
            isValid = false;
        }
        if (lastName == null || lastName.trim().isEmpty()) {
            isValid = false;
        }
        if (!isValidBirthDate(birthDate)) {
            isValid = false;
        }
        return isValid;
    }



    /**
     * Genera una representación en texto de la persona.
     * @return Cadena con los datos principales de la persona
     */
    @Override
    public String toString() {
        return "Person ID: " + personId + ", First Name: " + firstName +
                ", Last Name: " + lastName + ", Birth Date: " + birthDate;
    }
}
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
 * <h3>Características principales:</h3>
 * <ul>
 *   <li>ID único autogenerado y seguro para concurrencia</li>
 *   <li>Validación automática en setters</li>
 *   <li>Métodos de validación para reglas de negocio</li>
 *   <li>Persistencia simulada mediante el método {@link #save(List)}</li>
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
     * Si el nombre es nulo o vacío, no se asigna y se registra advertencia.
     *
     * @param firstName Nombre a asignar
     */
    public void setFirstName(String firstName) {
        if (firstName != null && !firstName.trim().isEmpty()) {
            this.firstName = firstName.trim();
        } else {
            logger.warn("Nombre vacío o nulo. No se asigna.");
        }
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
     * Si el apellido es nulo o vacío, no se asigna y se registra advertencia.
     *
     * @param lastName Apellido a asignar
     */
    public void setLastName(String lastName) {
        if (lastName != null && !lastName.trim().isEmpty()) {
            this.lastName = lastName.trim();
        } else {
            logger.warn("Apellido vacío o nulo. No se asigna.");
        }
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
        if (isValidBirthDate(birthDate, errores)) {
            this.birthDate = birthDate;
        } else {
            logger.warn("Fecha de nacimiento inválida: {}. Motivo: {}", birthDate, errores);
        }
    }

    /**
     * Valida si una fecha de nacimiento es válida según las reglas (no puede ser futura).
     * Agrega mensajes de error a la lista proporcionada si la fecha no es válida.
     *
     * @param bdate Fecha de nacimiento a validar
     * @param errorList Lista donde se agregarán los mensajes de error encontrados
     * @return {@code true} si la fecha es válida, {@code false} en caso contrario
     */
    public boolean isValidBirthDate(LocalDate bdate, List<String> errorList) {
        if (bdate == null) {
            return true;
        }
        if (bdate.isAfter(LocalDate.now())) {
            errorList.add("Birth date must not be in future.");
            return false;
        }
        return true;
    }

    /**
     * Valida si la persona es válida: nombre y apellido no vacíos, fecha válida.
     * Agrega mensajes de error a la lista proporcionada si hay datos incorrectos.
     *
     * @param errorList Lista para mensajes de error
     * @return {@code true} si todos los datos son válidos, {@code false} si hay errores
     */
    public boolean isValidPerson(List<String> errorList) {
        boolean isValid = true;
        if (firstName == null || firstName.trim().isEmpty()) {
            errorList.add("First name must contain minimum one character.");
            isValid = false;
        }
        if (lastName == null || lastName.trim().isEmpty()) {
            errorList.add("Last name must contain minimum one character.");
            isValid = false;
        }
        if (!isValidBirthDate(birthDate, errorList)) {
            isValid = false;
        }
        return isValid;
    }

    /**
     * Simula la persistencia de la persona.
     * Guarda la persona si pasa la validación; si hay errores, no guarda y los registra.
     *
     * @param errorList Lista donde se agregarán los mensajes de error encontrados durante la validación
     * @return {@code true} si la persona fue guardada exitosamente, {@code false} si hubo errores de validación
     */
    public boolean save(List<String> errorList) {
        if (isValidPerson(errorList)) {
            logger.info("Persona guardada: {}", this);
            return true;
        } else {
            logger.warn("No se guardó la persona por errores de validación: {}", errorList);
            return false;
        }
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
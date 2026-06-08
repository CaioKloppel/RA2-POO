package modelo;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class Consulta implements Serializable {
    private LocalDate data;
    private LocalTime horario;
    private int codigoMedico;
    private String cpfPaciente;

    public Consulta(LocalDate data, LocalTime horario, int codigoMedico, String cpfPaciente) {
        this.data = data;
        this.horario = horario;
        this.codigoMedico = codigoMedico;
        this.cpfPaciente = cpfPaciente;
    }

    public LocalDate getData() { return data; }
    public LocalTime getHorario() { return horario; }
    public int getCodigoMedico() { return codigoMedico; }
    public String getCpfPaciente() { return cpfPaciente; }

    public LocalDateTime getDataHora() {
        return LocalDateTime.of(data, horario);
    }

    @Override
    public String toString() {
        return data + " " + horario + " | médico " + codigoMedico + " | paciente " + cpfPaciente;
    }
}

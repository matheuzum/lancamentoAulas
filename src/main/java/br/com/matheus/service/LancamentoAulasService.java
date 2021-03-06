package br.com.matheus.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.com.matheus.domain.HorarioEscolar;
import br.com.matheus.domain.Materia;
import br.com.matheus.domain.Ocorrencia;

public class LancamentoAulasService {
	
    private static final Logger logger = LogManager.getLogger(LancamentoAulasService.class.getName());
    
    //TODO: Verificar como carregar a lista de feriados. carregar como @Resource?
    private List<LocalDate> feriados;
    
  //TODO: Verificar como carregar a lista de feriados. carregar como @Resource?
    private List<LocalDate> sabadosTrabalhados;
    
    public LancamentoAulasService(List<LocalDate> feriados, List<LocalDate> sabadosTrabalhados) {
    	this.feriados = feriados;
    	this.sabadosTrabalhados = sabadosTrabalhados;
    }

	public int getQuantidadeAula(String nomeMateria, HorarioEscolar horarioEscolar, LocalDate inicio, LocalDate fim) {
		//TODO: validar se a data de inicio é menor que a data fim
		int quantidadeAula = 0;
		Materia materia = horarioEscolar.getMateriaPorNome(nomeMateria);
		while(inicio.isBefore(fim) || inicio.isEqual(fim)) {
			for (Ocorrencia ocorrencia : materia.getDiasQueOcorre()) {
				if (ocorrencia.getDiaSemana().equals(inicio.getDayOfWeek())) {
					if (!isFeriado(inicio)) {
						quantidadeAula += ocorrencia.getQuantidadeAula();
						logger.debug("Encontrado Ocorrencia da materia {} para o dia {} - {} qtde aula {}", 
								nomeMateria, inicio.toString(), inicio.getDayOfWeek(), ocorrencia.getQuantidadeAula());
					} else {
						logger.debug("A Ocorrencia da materia {} para o dia {} - {} e um feriado", nomeMateria, inicio.toString(), inicio.getDayOfWeek());
					}
					continue;
				}
			}
			inicio = inicio.plusDays(1);
		}
		
		return quantidadeAula;
	}
	
	public int getQuantidadeDiaUtil(LocalDate inicio, LocalDate fim) {
		//TODO: validar se a data de inicio é menor que a data fim
		int quantidadeDia = 0;

		while(inicio.isBefore(fim) || inicio.isEqual(fim)) {
			if (isFeriado(inicio)) {
				logger.debug("Dia {} é um feriado", inicio);
			} else if (isWeekenDay(inicio.getDayOfWeek()) && !sabadoTrabalhado(inicio)) {
				logger.debug("Dia {} é um final de semana {}", inicio, inicio.getDayOfWeek());
			} else {
				quantidadeDia ++;
				logger.debug("Dia {} é um dia útil", inicio);
			}
			inicio = inicio.plusDays(1);
		}
		return quantidadeDia;
	}
	
	private boolean sabadoTrabalhado(LocalDate data) {
		for (LocalDate sabado : sabadosTrabalhados) {
			if (sabado.isEqual(data)) {
				return true;
			}
		}
		return false;
	}

	private boolean isWeekenDay(DayOfWeek dayOfWeek) {
		if (dayOfWeek.equals(DayOfWeek.SATURDAY) || dayOfWeek.equals(DayOfWeek.SUNDAY)) {
			return true;
		} else {
			return false;
		}
	}
	
	public Map<Materia, Integer> getQuantidadeAulas() {
		return null;
	}
	
	private boolean isFeriado(LocalDate data) {
		for (LocalDate feriado : feriados) {
			if (feriado.isEqual(data)) {
				return true;
			}
		}
		return false;
	}
}

package br.edu.utfpr.alunos.projetotaskmanager.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.Date;
import java.text.SimpleDateFormat;

@Entity(tableName = "tarefas")
public class Tarefa implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private String titulo;
    private Date data;
    private String localizacao;
    private String prioridade;
    private String periodo;
    private String descricao;
    private boolean concluido;

    public Tarefa(String titulo, Date data, String localizacao, String prioridade, String periodo, String descricao, boolean concluido) {
        this.titulo = titulo;
        this.data = data;
        this.localizacao = localizacao;
        this.prioridade = prioridade;
        this.periodo = periodo;
        this.descricao = descricao;
        this.concluido = concluido;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public String getLocalizacao() {
        return localizacao;
    }

    public void setLocalizacao(String localizacao) {
        this.localizacao = localizacao;
    }

    public String getPrioridade() {
        return prioridade;
    }

    public void setPrioridade(String prioridade) {
        this.prioridade = prioridade;
    }

    public String getPeriodo() {
        return periodo;
    }

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public boolean getConcluido() {
        return concluido;
    }

    public void setConcluido(boolean concluido) {
        this.concluido = concluido;
    }

    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String dataFormatada = sdf.format(getData());

        return "Tarefa: " + getTitulo() + " \n" +
                "Data: " + dataFormatada + " \n" +
                "Localizacao: " + getLocalizacao() + " \n" +
                "Prioridade: " + getPrioridade() + " \n" +
                "Periodo: " + getPeriodo() + " \n" +
                "Descricao: " + getDescricao() + " \n" +
                "Concluido? - " + (getConcluido() ? "Nao" : "Sim");
    }
}

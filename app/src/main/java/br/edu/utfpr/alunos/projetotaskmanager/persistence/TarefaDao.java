package br.edu.utfpr.alunos.projetotaskmanager.persistence;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import br.edu.utfpr.alunos.projetotaskmanager.model.Tarefa;

@Dao
public interface TarefaDao {
    @Query("SELECT * FROM tarefas")
    List<Tarefa> getAll();

    @Query("SELECT * FROM tarefas WHERE id = :id LIMIT 1")
    Tarefa findById(int id);

    @Insert
    void insertAll(Tarefa... tarefas);

    @Update
    void update(Tarefa tarefa);

    @Delete
    void delete(Tarefa tarefa);
}

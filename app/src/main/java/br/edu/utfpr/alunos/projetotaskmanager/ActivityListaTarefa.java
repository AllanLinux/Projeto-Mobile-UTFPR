package br.edu.utfpr.alunos.projetotaskmanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.app.AlertDialog;
import android.content.DialogInterface;

import android.content.Intent;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import br.edu.utfpr.alunos.projetotaskmanager.model.Tarefa;
import br.edu.utfpr.alunos.projetotaskmanager.persistence.AppDatabase;
import br.edu.utfpr.alunos.projetotaskmanager.persistence.TarefaDao;

public class ActivityListaTarefa extends AppCompatActivity {

    ArrayList<Tarefa> tarefas;
    private ListView listViewTarefas;
    private ActionMode currentActionMode;
    private int currentSelectedPosition = -1;
    private boolean novoItem;
    private static final String ARQUIVO = "br.edu.utfpr.alunos.sharedpreferences.PREFERENCIA_TEMA";
    private static final String TEMA = "TEMA";
    private int opcao = AppCompatDelegate.MODE_NIGHT_NO;
    private TarefaDao tarefaDao;
    private ExecutorService executorService;

    // Método chamado quando a atividade é iniciada pela primeira vez
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_tarefa);

        tarefas = new ArrayList<>();

        AppDatabase db = AppDatabase.getDatabase(this);
        tarefaDao = db.tarefaDao();

        listViewTarefas = findViewById(R.id.listViewTarefas);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        setTitle(getString(R.string.lista_tarefas));
        executorService = Executors.newSingleThreadExecutor();
        atualizarListaTarefas(); // Troque popularListView por atualizarListaTarefas
        registerForContextMenu(listViewTarefas);
        lerPreferenciaTema();
    }

    //Método para atualizar a lista de tarefas. Ele recupera as tarefas do banco de dados em uma thread
    // separada e atualiza a lista de tarefas na interface do usuário em uma thread principal.
    private void atualizarListaTarefas() {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                List<Tarefa> tarefasDoBanco = tarefaDao.getAll();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tarefas.clear();
                        tarefas.addAll(tarefasDoBanco);
                        popularListView();
                        currentSelectedPosition = -1;
                    }
                });
            }
        });
    }

    // Método para ler a preferência do tema. Ele recupera a opção de tema armazenada
    // nas preferências compartilhadas e chama o método mudarTema() para atualizar o tema da atividade
    private void lerPreferenciaTema() {
        SharedPreferences shared = getSharedPreferences(ARQUIVO, Context.MODE_PRIVATE);
        opcao = shared.getInt(TEMA, opcao);
        mudarTema();
    }

    // Método para mudar o tema
    private void mudarTema() {
        AppCompatDelegate.setDefaultNightMode(opcao);
    }

    // Método para salvar a preferência de cor do tema.
    // Ele armazena a nova opção de tema nas preferências compartilhadass e chama o método mudarTema() para atualizar o tema
    private void salvarPreferenciaCor(int novoValor) {
        SharedPreferences shared = getSharedPreferences(ARQUIVO, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shared.edit();
        editor.putInt(TEMA, novoValor);
        editor.apply();
        opcao = novoValor;
        mudarTema();
    }

    // Método para popular a lista de tarefas na ListView.
    // Cria um adaptador personalizado (TarefaAdapter) com a lista de tarefas e associa o adaptador a ListView.
    // Define um Listener do clique de um item para a ListView.
    @SuppressLint("SimpleDateFormat")
    private void popularListView() {
        Log.d("DEBUG", "popularListView: start");  // Log de início
        // Crie um adaptador com a lista de tarefas
        TarefaAdapter adapter = new TarefaAdapter(this, tarefas);

        Log.d("DEBUG", "popularListView: adapter set");

        // Associe o adaptador à ListView
        listViewTarefas.setAdapter(adapter);

        // Defina o ouvinte de clique do item para a ListView
        listViewTarefas.setOnItemClickListener((parent, view, position, id) -> {
            // Se já estiver no modo de ação contextual, apenas retorne
            if (currentActionMode != null) {
                return;
            }

            // Atualize a posição do item selecionado
            currentSelectedPosition = position;

            // Inicie o modo de ação contextual e defina a visão como selecionada
            currentActionMode = startActionMode(actionModeCallback);
            view.setSelected(true);
        });
        Log.d("DEBUG", "popularListView: end");  // Log de fim
    }

    // define o menu de contexto e trata os eventos de clique nos itens do menu.
    // Permite editar ou remover uma tarefa selecionada
    private ActionMode.Callback actionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.menu_contextual, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            if (item.getItemId() == R.id.menuItemEditar) {
                editarTarefa(currentSelectedPosition);
                mode.finish(); // Fecha o menu de ação contextual
                return true;
            } else if (item.getItemId() == R.id.menuItemRemover) {
                removerTarefa(currentSelectedPosition);
                mode.finish(); // Fecha o menu de ação contextual
                return true;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            currentActionMode = null;
        }
    };

    // Método para editar uma tarefa selecionada.
    private void editarTarefa(int position) {
        Log.d("DEBUG", "editarTarefa: start");  // Log de início
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                Tarefa tarefa = tarefaDao.getAll().get(position);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(ActivityListaTarefa.this, ActivityCadastroTarefa.class);
                        intent.putExtra("tarefa", tarefa);
                        novoItem = false; // Adicionado para indicar que este não é um novo item
                        startActivityForResult(intent, 1);
                    }
                });
            }
        });
        Log.d("DEBUG", "editarTarefa: finish");  // Log de início
    }

    // Método para iniciar a ActivitySobre. Ele cria uma instância da atividade
    public void sobreActivity() {
        Intent intent = new Intent(this, ActivitySobre.class);
        startActivity(intent);
    }

    public void cadastroTarefaActivity() {
        Intent intent = new Intent(this, ActivityCadastroTarefa.class);
        novoItem = true;
        startActivityForResult(intent, 1);
    }

    // Método chamado quando uma atividade retorna um resultado.
    // Ele recupera a tarefa editada ou adicionada da atividade ActivityCadastroTarefa e atualiza a lista de tarefas do banco de dados.
    // Em seguida, atualiza a lista de tarefas na interface do usuário.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            final Tarefa tarefa = (Tarefa) data.getSerializableExtra("tarefaObj");
            if (tarefa != null) {
                executorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        if (!novoItem && currentSelectedPosition >= 0 && currentSelectedPosition < tarefaDao.getAll().size()) {
                            tarefaDao.update(tarefa);
                        } else {
                            tarefaDao.insertAll(tarefa);
                        }
                        List<Tarefa> tarefasDoBanco = tarefaDao.getAll();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tarefas.clear();
                                tarefas.addAll(tarefasDoBanco);
                                popularListView();
                                currentSelectedPosition = -1;
                            }
                        });
                    }
                });
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_item_listagem, menu);
        return true;
    }

    // Método para tratar o evento de clique de cada item do menu.
    // Ele verifica qual item do menu foi selecionado e chama o método correspondente.
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        final int menuId = item.getItemId();

        if (menuId == R.id.menuItemAdicionar) {
            cadastroTarefaActivity();
            return true;
        } else if (menuId == R.id.menuItemSobre) {
            sobreActivity();
            return true;
        } else if (menuId == R.id.menuItemTema_Claro) {
            opcao = AppCompatDelegate.MODE_NIGHT_NO;
            salvarPreferenciaCor(opcao);
            return true;
        } else if (menuId == R.id.menuItemTema_Escuro) {
            opcao = AppCompatDelegate.MODE_NIGHT_YES;
            salvarPreferenciaCor(opcao);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    // Método para remover uma tarefa selecionada.
    // Ele exibe um diálogo de confirmação para o usuário e, se confirmado, remove a tarefa do banco de dados em uma thread separada e atualiza a lista de tarefas na interface do usuário.
    private void removerTarefa(int position) {
        Tarefa tarefa = tarefas.get(position);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.confirmar_exclusao);
        builder.setMessage(R.string.msg_dialog_exclusao);
        builder.setPositiveButton(R.string.excluir, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                executorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        tarefaDao.delete(tarefa);
                        List<Tarefa> tarefasDoBanco = tarefaDao.getAll();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tarefas.clear();
                                tarefas.addAll(tarefasDoBanco);
                                popularListView();
                            }
                        });
                    }
                });
            }
        });
        builder.setNegativeButton("Cancelar", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // Método para preparar o menu de opções antes de ser exibido.
    // Ele verifica a opção de tema atual e marca o item de menu correspondente como selecionado.
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item;
        switch (opcao) {
            case AppCompatDelegate.MODE_NIGHT_YES:
                item = menu.findItem(R.id.menuItemTema_Escuro);
                break;
            case AppCompatDelegate.MODE_NIGHT_NO:
                item = menu.findItem(R.id.menuItemTema_Claro);
                break;
            default:
                return false;
        }
        item.setChecked(true);
        return true;
    }
}

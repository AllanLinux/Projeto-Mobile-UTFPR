package br.edu.utfpr.alunos.projetotaskmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.widget.DatePicker;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import br.edu.utfpr.alunos.projetotaskmanager.model.Tarefa;
import br.edu.utfpr.alunos.projetotaskmanager.utils.UtilsDate;

public class ActivityCadastroTarefa extends AppCompatActivity {

    private ArrayAdapter<String> spinnerAdapter;
    private Spinner spinnerPeriodo;
    private EditText editTextTitulo;
    private EditText editTextData;
    private EditText editTextLocalizacao;
    private RadioGroup radioGroupPrioridade;
    private EditText editTextDescricao;
    private CheckBox checkBoxDesabilitado;
    private Tarefa tarefa;
    String prioridade = "Não definido";
    private static final String TAG = ActivityCadastroTarefa.class.getSimpleName();

    // Método chamado quando a atividade é iniciada pela primeira vez
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_tarefa);

        spinnerPeriodo = findViewById(R.id.spinnerPeriodo);
        editTextTitulo = findViewById(R.id.editTextTitulo);
        editTextData = findViewById(R.id.editTextData);
        editTextData.setOnClickListener(v -> exibirDatePickerDialog());
        editTextLocalizacao = findViewById(R.id.editTextLocalizacao);
        radioGroupPrioridade = findViewById(R.id.radioGroupPrioridade);
        editTextDescricao = findViewById(R.id.editTextDescricao);
        checkBoxDesabilitado = findViewById(R.id.checkBoxDesativado);

        setTitle(getString(R.string.dados_tarefa));

        spinnerElementos();

        Intent intent = getIntent();
        Tarefa tarefa = (Tarefa) intent.getSerializableExtra("tarefa");
        if (tarefa != null) {
            editTextTitulo.setText(tarefa.getTitulo());
            editTextData.setText(new SimpleDateFormat("dd/MM/yyyy").format(tarefa.getData()));
            editTextLocalizacao.setText(tarefa.getLocalizacao());
            editTextDescricao.setText(tarefa.getDescricao());
            checkBoxDesabilitado.setChecked(tarefa.getConcluido());

            if ("Baixa".equals(tarefa.getPrioridade())) {
                radioGroupPrioridade.check(R.id.radioButtonPrioridade_Baixa);
            } else if ("Media".equals(tarefa.getPrioridade())) {
                radioGroupPrioridade.check(R.id.radioButtonPrioridade_Media);
            } else if ("Alta".equals(tarefa.getPrioridade())) {
                radioGroupPrioridade.check(R.id.radioButtonPrioridade_Alta);
            }

            int spinnerPosition = spinnerAdapter.getPosition(tarefa.getPeriodo());
            spinnerPeriodo.setSelection(spinnerPosition);

            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.periodo_array, android.R.layout.simple_spinner_item);
            spinnerPeriodo.setSelection(spinnerPosition);
        }
    }

    // Método para preencher o Spinner com os períodos
    private void spinnerElementos() {
        ArrayList<String> periodos = new ArrayList<>();
        periodos.add("");
        periodos.add(getString(R.string.manha));
        periodos.add(getString(R.string.tarde));
        periodos.add(getString(R.string.noite));

        spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, periodos);

        spinnerPeriodo.setAdapter(spinnerAdapter);
    }

    // Método para limpar os campos do formulário
    public void limparCampos() {
        editTextTitulo.setText("");
        editTextData.setText("");
        editTextLocalizacao.setText("");
        editTextDescricao.setText("");
        radioGroupPrioridade.clearCheck();
        spinnerPeriodo.setSelection(0);
        checkBoxDesabilitado.setChecked(false);
        Toast.makeText(this, getString(R.string.campos_limpos), Toast.LENGTH_LONG).show();
    }

    // Método para salvar a Tarefa
    public void salvarTarefa() {

        Intent intent = new Intent(this, ActivityListaTarefa.class);

        String titulo = editTextTitulo.getText().toString();
        Date data = convertStringToDate(editTextData.getText().toString());
        String localizacao = editTextLocalizacao.getText().toString();
        prioridade = "Não definido";
        int checkedId = radioGroupPrioridade.getCheckedRadioButtonId();

        if (checkedId == R.id.radioButtonPrioridade_Baixa) {
            prioridade = getString(R.string.prioridade_baixa);
        } else if (checkedId == R.id.radioButtonPrioridade_Media) {
            prioridade = getString(R.string.prioridade_media);;
        } else if (checkedId == R.id.radioButtonPrioridade_Alta) {
            prioridade = getString(R.string.prioridade_alta);;
        }

        String periodo = spinnerPeriodo.getSelectedItem().toString();
        String descricao = editTextDescricao.getText().toString();
        boolean concluido = checkBoxDesabilitado.isChecked();

        if (titulo.isEmpty() || data == null || localizacao.isEmpty() || "Não definido".equals(prioridade) || periodo.isEmpty() || descricao.isEmpty()) {
            Toast.makeText(this, getString(R.string.preencha_campos), Toast.LENGTH_LONG).show();
        } else if (data == null) {
            Toast.makeText(this, getString(R.string.data_invalida), Toast.LENGTH_LONG).show();
        } else {
            try {
                tarefa = new Tarefa(titulo, data, localizacao, prioridade, periodo, descricao, concluido);
                Toast.makeText(this, getString(R.string.item_salvo), Toast.LENGTH_LONG).show();

                Intent resultIntent = new Intent();
                resultIntent.putExtra("tarefaObj", tarefa);
                setResult(RESULT_OK, resultIntent);
                finish();
            } catch (Exception e){
                Log.d(TAG, e.getMessage());
            }
        }
    }


    // Método para cancelar o cadastro da Tarefa
    public void cancelar() {
        onBackPressed();
    }

    //Método chamado quando o botão de voltar é pressionado
    @Override
    public void onBackPressed() {
        Intent resultIntent = new Intent();
        setResult(RESULT_CANCELED, resultIntent);
        super.onBackPressed();
    }

    // Método para converter uma string no formato "dd/MM/yyyy" para um objeto Date
    private Date convertStringToDate(String dateString) {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        try {
            return format.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Método para adicionar menu na tela
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_item_cadastro, menu);
        return true;
    }

    // Método para tratar o evento de clique de cada item do menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int menuId = item.getItemId();

        if (menuId == R.id.menuItemCadastro_Salvar) {
            salvarTarefa();
            return true;
        } else if (menuId == R.id.menuItemCadastro_Limpar) {
            limparCampos();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    // Método para exibir o DatePickerDialog para selecionar a data
    private void exibirDatePickerDialog() {
        // Obter a data atual
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        // Criar o DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, yearSelected, monthOfYear, dayOfMonthSelected) -> {
                    // Atualizar o EditText com a data selecionada
                    Calendar selectedCalendar = Calendar.getInstance();
                    selectedCalendar.set(yearSelected, monthOfYear, dayOfMonthSelected);
                    Date selectedDate = selectedCalendar.getTime();
                    String formattedDate = UtilsDate.formatDate(ActivityCadastroTarefa.this, selectedDate);
                    editTextData.setText(formattedDate);
                },
                year,
                month,
                dayOfMonth
        );

        // Exibir o DatePickerDialog
        datePickerDialog.show();
    }

}

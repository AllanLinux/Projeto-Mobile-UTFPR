package br.edu.utfpr.alunos.projetotaskmanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

import br.edu.utfpr.alunos.projetotaskmanager.model.Tarefa;

public class TarefaAdapter extends ArrayAdapter<Tarefa> {

    private Context mContext;

    public TarefaAdapter(Context context, List<Tarefa> tarefas) {
        super(context, 0, tarefas);
        mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Tarefa tarefa = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.activity_item_tarefa, parent, false);
        }

        TextView tvTarefa = convertView.findViewById(R.id.tvTarefa);
        TextView tvData = convertView.findViewById(R.id.tvData);
        TextView tvLocalizacao = convertView.findViewById(R.id.tvLocalizacao);
        TextView tvPrioridade = convertView.findViewById(R.id.tvPrioridade);
        TextView tvPeriodo = convertView.findViewById(R.id.tvPeriodo);
        TextView tvDescricao = convertView.findViewById(R.id.tvDescricao);
        TextView tvConcluido = convertView.findViewById(R.id.tvConcluido);

        tvTarefa.setText(tarefa.getTitulo());

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String dataFormatada = sdf.format(tarefa.getData());
        tvData.setText(dataFormatada);

        tvLocalizacao.setText(tarefa.getLocalizacao());
        tvPrioridade.setText(tarefa.getPrioridade());
        tvPeriodo.setText(tarefa.getPeriodo());
        tvDescricao.setText(tarefa.getDescricao());
        tvConcluido.setText(tarefa.getConcluido() ? mContext.getString(R.string.sim) : mContext.getString(R.string.nao));

        return convertView;
    }
}

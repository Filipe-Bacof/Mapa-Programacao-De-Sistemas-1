import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Programa para manipulação de registros de doações de sangue em um arquivo
 * CSV.
 * O programa permite visualizar, inserir e remover registros.
 * 
 * Funcionalidades:
 * 1. Ler o conteúdo de um arquivo CSV.
 * 2. Inserir uma nova doação no arquivo.
 * 3. Remover uma doação pelo código.
 * 
 * O programa oferece um menu para a seleção das funcionalidades.
 * 
 * @author Filipe Bacof
 */
public class ManipuladorCSV {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String caminhoArquivo = null;
        boolean arquivoValido = false;

        do {
            System.out.println("Informe o caminho do arquivo CSV (ex: C:/CSV/doacoes.csv): ");
            caminhoArquivo = scanner.nextLine();
            if (verificarArquivo(caminhoArquivo)) {
                System.out.println("Arquivo lido com sucesso!");
                arquivoValido = true;
            } else {
                System.out.println("Falha em ler o arquivo, digite o caminho novamente.");
            }
        } while (!arquivoValido);

        int opcao;
        do {
            System.out.println("\nMenu:");
            System.out.println("1 - Visualizar registros");
            System.out.println("2 - Inserir nova doação");
            System.out.println("3 - Remover doação");
            System.out.println("0 - Sair");
            System.out.print("Selecione uma opção: ");
            opcao = scanner.nextInt();
            scanner.nextLine();

            switch (opcao) {
                case 1:
                    lerCSV(caminhoArquivo);
                    break;
                case 2:
                    inserirDoacao(caminhoArquivo, scanner);
                    break;
                case 3:
                    System.out.print("Informe o código da doação a ser removida: ");
                    int codigoRemover = scanner.nextInt();
                    removerDoacao(caminhoArquivo, codigoRemover);
                    break;
                case 0:
                    System.out.println("Saindo...");
                    break;
                default:
                    System.out.println("Opção inválida.");
            }
        } while (opcao != 0);

        scanner.close();
    }

    /**
     * Verifica se o arquivo pode ser lido corretamente.
     * 
     * @param caminhoArquivo Caminho do arquivo CSV.
     * @return true se o arquivo for lido com sucesso, false caso contrário.
     */
    public static boolean verificarArquivo(String caminhoArquivo) {
        try (BufferedReader br = new BufferedReader(new FileReader(caminhoArquivo))) {
            // Verifica se consegue ler a primeira linha do CSV pelo menos
            if (br.readLine() != null) {
                return true;
            }
        } catch (IOException e) {
            return false;
        }
        return false;
    }

    /**
     * Lê o conteúdo do arquivo CSV e exibe cada linha no console.
     * 
     * @param caminhoArquivo Caminho do arquivo CSV.
     */
    public static void lerCSV(String caminhoArquivo) {
        try (BufferedReader br = new BufferedReader(new FileReader(caminhoArquivo))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                System.out.println(linha);
            }
        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo: " + e.getMessage());
        }
    }

    /**
     * Insere uma nova doação no final do arquivo CSV.
     * 
     * @param caminhoArquivo Caminho do arquivo CSV.
     * @param scanner        Scanner para entrada de dados do usuário.
     */
    public static void inserirDoacao(String caminhoArquivo, Scanner scanner) {
        try {
            boolean precisaDeNovaLinha = true;
            try (BufferedReader br = new BufferedReader(new FileReader(caminhoArquivo))) {
                String ultimaLinha = null, linhaAtual;
                while ((linhaAtual = br.readLine()) != null) {
                    ultimaLinha = linhaAtual;
                }
                if (ultimaLinha != null && !ultimaLinha.isEmpty()) {
                    precisaDeNovaLinha = false;
                }
            } catch (IOException e) {
                System.err.println("Erro ao verificar o arquivo: " + e.getMessage());
            }

            try (BufferedWriter bw = new BufferedWriter(new FileWriter(caminhoArquivo, true))) {
                if (!precisaDeNovaLinha) {
                    bw.newLine();
                }

                System.out.println("Informe os dados da nova doação:");
                System.out.print("Código: ");
                int codigo = scanner.nextInt();
                scanner.nextLine();

                System.out.print("Nome: ");
                String nome = scanner.nextLine();

                System.out.print("CPF (111222333-44): ");
                String cpf = scanner.nextLine();

                System.out.print("Data de nascimento (AAAA-MM-DD): ");
                String dataNascimento = scanner.nextLine();

                System.out.print("Tipo sanguíneo (exemplo: A+): ");
                String tipoSanguineo = scanner.nextLine();

                System.out.print("Quantidade de sangue (ml): ");
                int quantidade = scanner.nextInt();

                String novaLinha = String.format("%d,%s,%s,%s,%s,%d", codigo, nome, cpf, dataNascimento, tipoSanguineo,
                        quantidade);
                bw.write(novaLinha);
                bw.newLine();
                System.out.println("Doação inserida com sucesso!");
            }
        } catch (IOException e) {
            System.err.println("Erro ao inserir a doação: " + e.getMessage());
        }
    }

    /**
     * Remove uma doação do arquivo CSV com base no código informado.
     * 
     * @param caminhoArquivo Caminho do arquivo CSV.
     * @param codigo         Código da doação a ser removida.
     */
    public static void removerDoacao(String caminhoArquivo, int codigo) {
        List<String> linhas = new ArrayList<>();
        boolean removido = false;

        try (BufferedReader br = new BufferedReader(new FileReader(caminhoArquivo))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                if (!linha.startsWith(String.valueOf(codigo) + ",")) {
                    linhas.add(linha);
                } else {
                    removido = true;
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo: " + e.getMessage());
            return;
        }

        // Reescreve o arquivo porém sem a linha que foi solicitada a remoção
        if (removido) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(caminhoArquivo))) {
                for (String l : linhas) {
                    bw.write(l);
                    bw.newLine();
                }
                System.out.println("Doação removida com sucesso!");
            } catch (IOException e) {
                System.err.println("Erro ao escrever no arquivo: " + e.getMessage());
            }
        } else {
            System.out.println("Código não encontrado.");
        }
    }
}

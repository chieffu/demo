import lombok.Data;

import java.util.List;

@Data
public class Round {

    String roundId;
    String tableId;
    List<String> banker;
    List<List<String>> players;
}

package np.sumit.PersonalExpenseTrackerAPI.dto.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import np.sumit.PersonalExpenseTrackerAPI.entity.Category;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonPropertyOrder({
        "total",
        "categories"
})
public class ExpenseSummaryResponseDto {
    private Double total;
    private Map<Category, Double> categories;
}

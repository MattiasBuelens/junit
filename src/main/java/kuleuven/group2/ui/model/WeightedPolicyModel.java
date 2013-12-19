package kuleuven.group2.ui.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import kuleuven.group2.policy.WeightedTestSortingPolicy;

public class WeightedPolicyModel {

	private final WeightedTestSortingPolicy weightedPolicy;
	private final ObjectProperty<PolicyModel> policy = new SimpleObjectProperty<>();
	private final IntegerProperty weight = new SimpleIntegerProperty();

	public WeightedPolicyModel(PolicyModel policy, int weight) {
		this.weightedPolicy = new WeightedTestSortingPolicy(policy.getPolicy(), weight);
		this.policy.set(policy);
		this.weight.set(weightedPolicy.getWeight());
		this.weight.addListener(new WeightChangeListener());
	}

	public WeightedTestSortingPolicy getWeightedPolicy() {
		return weightedPolicy;
	}

	public PolicyModel getPolicy() {
		return policyProperty().get();
	}

	public ReadOnlyObjectProperty<PolicyModel> policyProperty() {
		return policy;
	}

	public StringProperty policyNameProperty() {
		return getPolicy().nameProperty();
	}

	public int getWeight() {
		return weightProperty().get();
	}

	public void setWeight(int weight) {
		weightProperty().set(weight);
	}

	public IntegerProperty weightProperty() {
		return weight;
	}

	protected class WeightChangeListener implements ChangeListener<Number> {
		@Override
		public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
			int newWeight = newValue.intValue();
			weightedPolicy.setWeight(newWeight);
			// The new value may be invalid, so check the result
			int actualWeight = getWeightedPolicy().getWeight();
			if (newWeight != actualWeight) {
				setWeight(actualWeight);
			}
		}
	}

}

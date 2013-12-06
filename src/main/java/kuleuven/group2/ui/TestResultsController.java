package kuleuven.group2.ui;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.ListBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.binding.When;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Callback;
import kuleuven.group2.ui.model.TestBatchModel;
import kuleuven.group2.ui.model.TestRunModel;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;

public class TestResultsController {

	/*
	 * Date format for time stamp
	 */
	protected static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

	/*
	 * Images for test result
	 */
	protected static final Image IMAGE_SUCCESS = new Image(TestResultsController.class.getResource("icons/test-ok.png")
			.toExternalForm());
	protected static final Image IMAGE_FAILURE = new Image(TestResultsController.class
			.getResource("icons/test-err.png").toExternalForm());

	/*
	 * Components
	 */

	@FXML
	private TableView<TestBatchModel> batchesTable;

	@FXML
	private TableView<TestRunModel> runsTable;

	@FXML
	private Pane selectedRunDetail;

	@FXML
	private Label selectedRunTitle;

	@FXML
	private Label selectedRunTimeStamp;

	@FXML
	private Label selectedRunExceptionLabel;

	@FXML
	private Label selectedRunException;

	@FXML
	private Label selectedRunTraceLabel;

	@FXML
	private TextArea selectedRunTrace;

	/*
	 * Properties
	 */

	private final ListProperty<TestBatchModel> batches = new SimpleListProperty<>(
			FXCollections.<TestBatchModel> observableArrayList());
	private final ObjectProperty<TestBatchModel> selectedBatch = new SimpleObjectProperty<>();
	private final ObjectProperty<TestRunModel> selectedRun = new SimpleObjectProperty<>();

	public ListProperty<TestBatchModel> batchesProperty() {
		return batches;
	}

	public ObjectProperty<TestBatchModel> selectedBatchProperty() {
		return selectedBatch;
	}

	public ListBinding<TestRunModel> runsProperty() {
		return new ListBinding<TestRunModel>() {
			{
				super.bind(selectedBatchProperty());
			}

			@Override
			protected ObservableList<TestRunModel> computeValue() {
				TestBatchModel selectedBatch = selectedBatchProperty().get();
				if (selectedBatch == null) {
					return FXCollections.emptyObservableList();
				}
				return selectedBatchProperty().get().testRunsProperty();
			}
		};
	}

	public ObjectProperty<TestRunModel> selectedRunProperty() {
		return selectedRun;
	}

	public BooleanBinding selectedRun_isSuccessful() {
		return Bindings.selectBoolean(selectedRunProperty(), "successfulRun");
	}

	public BooleanBinding selectedRun_isFailed() {
		return Bindings.selectBoolean(selectedRunProperty(), "failedRun");
	}

	public ObjectBinding<Image> selectedRun_resultImage() {
		return new When(selectedRun_isSuccessful()).then(IMAGE_SUCCESS).otherwise(IMAGE_FAILURE);
	}

	public ObjectBinding<ImageView> selectedRun_resultImageView() {
		return new ObjectBinding<ImageView>() {
			{
				super.bind(selectedRunProperty());
			}

			@Override
			protected ImageView computeValue() {
				return new ImageView(selectedRun_resultImage().get());
			}
		};
	}

	public StringBinding selectedRun_testClassName() {
		return Bindings.selectString(selectedRunProperty(), "testClassName");
	}

	public StringBinding selectedRun_testMethodName() {
		return Bindings.selectString(selectedRunProperty(), "testMethodName");
	}

	public ObjectBinding<Date> selectedRun_timeStamp() {
		return Bindings.select(selectedRunProperty(), "timeStamp");
	}

	public StringBinding selectedRunTest_formattedTimeStamp() {
		return new StringBinding() {
			{
				super.bind(selectedRunProperty());
			}

			@Override
			protected String computeValue() {
				if (selectedRun_timeStamp().get() != null) {
					return DATE_FORMAT.format(selectedRun_timeStamp().get());
				} else {
					return null;
				}
			}
		};
	}

	public ObjectBinding<Throwable> selectedRun_exception() {
		return Bindings.select(selectedRunProperty(), "exception");
	}

	public ObjectBinding<StackTraceElement[]> selectedRun_trace() {
		return Bindings.select(selectedRunProperty(), "trace");
	}

	public StringBinding selectedRun_formattedTrace() {
		return new StringBinding() {
			{
				super.bind(selectedRunProperty());
			}

			@Override
			protected String computeValue() {
				if (selectedRun_trace().get() != null) {
					return Joiner.on('\n').join(selectedRun_trace().get());
				} else {
					return null;
				}
			}
		};
	}

	@FXML
	public void initialize() {
		setupBatches();
		setupRuns();
		setupDetail();
	}

	protected void setupBatches() {
		// Bind to model
		batchesTable.itemsProperty().bindBidirectional(batches);
		// Bind selected test batch
		selectedBatchProperty().bind(batchesTable.getSelectionModel().selectedItemProperty());

		// Set up columns
		TableColumn<TestBatchModel, Date> timestampColumn = new TableColumn<>("Time");
		timestampColumn.setCellValueFactory(new PropertyValueFactory<TestBatchModel, Date>("timestamp"));
		timestampColumn.setCellFactory(new TimestampCellFactory<TestBatchModel>());
		timestampColumn.setPrefWidth(120);

		batchesTable.getColumns().setAll(ImmutableList.of(timestampColumn));
	}

	protected void setupRuns() {
		// Bind to model
		runsTable.itemsProperty().bind(runsProperty());
		// Bind selected test run
		selectedRunProperty().bind(runsTable.getSelectionModel().selectedItemProperty());

		// Set up columns
		TableColumn<TestRunModel, Boolean> resultColumn = new TableColumn<>();
		resultColumn.setCellValueFactory(new PropertyValueFactory<TestRunModel, Boolean>("successfulRun"));
		resultColumn.setCellFactory(new ResultCellFactory<TestRunModel>());
		resultColumn.setPrefWidth(50);

		TableColumn<TestRunModel, String> testClassNameColumn = new TableColumn<>("Test class");
		testClassNameColumn.setCellValueFactory(new PropertyValueFactory<TestRunModel, String>("testClassName"));
		testClassNameColumn.setPrefWidth(100);

		TableColumn<TestRunModel, String> testMethodNameColumn = new TableColumn<>("Test method");
		testMethodNameColumn.setCellValueFactory(new PropertyValueFactory<TestRunModel, String>("testMethodName"));
		testMethodNameColumn.setPrefWidth(100);

		TableColumn<TestRunModel, Date> timestampColumn = new TableColumn<>("Time");
		timestampColumn.setCellValueFactory(new PropertyValueFactory<TestRunModel, Date>("timestamp"));
		timestampColumn.setCellFactory(new TimestampCellFactory<TestRunModel>());
		timestampColumn.setPrefWidth(120);

		runsTable.getColumns().setAll(
				ImmutableList.of(resultColumn, testClassNameColumn, testMethodNameColumn, timestampColumn));
	}

	protected void setupDetail() {
		// Bind to properties
		selectedRunTitle.textProperty().bind(
				selectedRun_testClassName().concat(".").concat(selectedRun_testMethodName()));
		selectedRunTitle.graphicProperty().bind(selectedRun_resultImageView());
		selectedRunTimeStamp.textProperty().bind(selectedRunTest_formattedTimeStamp());
		selectedRunException.textProperty().bind(Bindings.convert(selectedRun_exception()));
		selectedRunTrace.textProperty().bind(Bindings.concat(selectedRun_formattedTrace()));

		// Show only when selected
		selectedRunDetail.visibleProperty().bind(selectedRunProperty().isNotNull());
		selectedRunDetail.managedProperty().bind(selectedRunProperty().isNotNull());

		// Show only exception details when failed
		selectedRunExceptionLabel.visibleProperty().bind(selectedRun_isFailed());
		selectedRunExceptionLabel.managedProperty().bind(selectedRun_isFailed());
		selectedRunException.visibleProperty().bind(selectedRun_isFailed());
		selectedRunException.managedProperty().bind(selectedRun_isFailed());
		selectedRunTraceLabel.visibleProperty().bind(selectedRun_isFailed());
		selectedRunTraceLabel.managedProperty().bind(selectedRun_isFailed());
		selectedRunTrace.visibleProperty().bind(selectedRun_isFailed());
		selectedRunTrace.managedProperty().bind(selectedRun_isFailed());
	}

	/**
	 * Formats a date table column using the given {@link DateFormat}.
	 */
	protected static class TimestampCellFactory<T> implements Callback<TableColumn<T, Date>, TableCell<T, Date>> {
		@Override
		public TableCell<T, Date> call(TableColumn<T, Date> column) {
			return new TableCell<T, Date>() {
				@Override
				protected void updateItem(Date date, boolean empty) {
					super.updateItem(date, empty);
					if (!empty) {
						setText(DATE_FORMAT.format(date));
					} else {
						setText(null);
					}
				}
			};
		}
	}

	/**
	 * Puts a success or failure image in a boolean table column cell.
	 */
	protected static class ResultCellFactory<T> implements Callback<TableColumn<T, Boolean>, TableCell<T, Boolean>> {
		@Override
		public TableCell<T, Boolean> call(TableColumn<T, Boolean> column) {
			return new TableCell<T, Boolean>() {
				@Override
				protected void updateItem(Boolean result, boolean empty) {
					super.updateItem(result, empty);
					if (result != null) {
						setGraphic(new ImageView(result ? IMAGE_SUCCESS : IMAGE_FAILURE));
					} else {
						setGraphic(null);
					}
				}
			};
		}

	}

}

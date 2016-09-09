package dani.address.view;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

import com.google.common.base.Joiner;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import bean.categoria.CategoriaBean;
import bean.especie.EspecieBean;
import bean.procedencia.ProcedenciaBean;
import bean.tropa.AnimalBean;
import bean.tropa.TropaBean;
import dani.address.AplicacionPrincipalPalco;
import ejecutoras.faena.Etiqueta;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.Worker.State;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import tropa.TropaReservada;

public class PalcoController {

	@FXML
	private ComboBox<EspecieBean> especie;

	@FXML
	private ComboBox<ProcedenciaBean> procedencia;

	@FXML
	private RadioButton rbEntera;

	@FXML
	private RadioButton rbAlMedio;

	@FXML
	private TextField numeroTropa;

	@FXML
	private TextField numeroGarron;

	@FXML
	private TextField pesoAnimal;

	@FXML
	private AnchorPane primerPanel;

	@FXML
	private AnchorPane segundoPanel;

	@FXML
	private Pane tercerPanel;

	@FXML
	private HBox categoriaContainer;

	@FXML
	private ToggleGroup cabeza = new ToggleGroup();

	@FXML
	private ToggleGroup categoriaToggleGroup = new ToggleGroup();

	@FXML
	private Button inicializarFaenaButton;

	@FXML
	private Button imprimirEtiqueta;

	@FXML
	private Button reservarGarronButton;

	public List<ToggleButton> botonesCategoria = new ArrayList<ToggleButton>();

	// Reference to the main application.
	private AplicacionPrincipalPalco aplicacionPrincipalPalco;
	private ObservableList<EspecieBean> especieList;
	private ObservableList<ProcedenciaBean> procedenciaList;
	private TropaReservada tropaReservada;
	private List<CategoriaBean> categoriasList;

	private static final String JSON_URL_ESPECIES = "http://localhost:8080/frigorifico/rest/especies";
	private static final String JSON_URL_PROCEDENCIAS = "http://localhost:8080/frigorifico/rest/procedencias";
	private static final String JSON_URL_CATEGORIAS = "http://localhost:8080/frigorifico/rest/categorias/";
	private static final String JSON_URL_SIGUIENTE_NUMERO_TROPA = "http://localhost:8080/frigorifico/rest/siguiente_tropa/";
	private static final String JSON_URL_GUARDAR_TROPA = "http://localhost:8080/frigorifico/rest/nueva_tropa_en_palco";
	private static final String JSON_URL_GUARDAR_ANIMAL = "http://localhost:8080/frigorifico/rest/agregar_animal_a_tropa";
	private static final String JSON_URL_OBTENER_GARRON = "http://localhost:8080/frigorifico/rest/obtener_siguiente_garron";
	private static final String JSON_URL_VERIFICAR_NUMERO_TROPA = "http://localhost:8080/frigorifico/rest/verificar_tropa_faenada/";
	private static final String JSON_URL_VERIFICAR_NUMERO_GARRON_MODIFICADO = "http://localhost:8080/frigorifico/rest/verificar_numero_garron_modificado/";
	private static final String JSON_URL_RESETEAR_NUMERO_TROPA = "http://localhost:8080/frigorifico/rest/resetear_numero_tropa/";
	
	private ExecutorService executorService = Executors.newCachedThreadPool();

	private ObtenerSiguienteNroTropaService obtenerSiguienteNroTropaService = new ObtenerSiguienteNroTropaService();
	private CargarCategoriasSegunEspecie cargarCategoriasSegunEspecieService = new CargarCategoriasSegunEspecie();
	private GuardarTropaService guardarTropaService = new GuardarTropaService();
	private ObtenerSiguienteGarronService obtenerSiguienteGarronService = new ObtenerSiguienteGarronService();
	private GuardarAnimalService guardarAnimalService = new GuardarAnimalService();
	private VerificarTropaFaenadaService verificarTropaFaenadaService = new VerificarTropaFaenadaService();
	private VerificarNumeroGarronModificadoService verificarNumeroGarronModificadoService = new VerificarNumeroGarronModificadoService();
	private ResetearNumeroTropaService resetearNumeroTropaService = new ResetearNumeroTropaService();
	
	private List<Integer> garronesReservados = new ArrayList<Integer>();

	private TropaBean tropaBeanPalcoController = new TropaBean();

	/**
	 * The constructor. The constructor is called before the initialize()
	 * method.
	 */
	public PalcoController() {
	}

	/**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml file has been loaded.
	 */
	@FXML
	private void initialize() {

		// FORMATO solo numeros del campo texto
		pesoAnimal.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				Pattern patron = Pattern.compile("\\b[0-9]+(?:\\,{0,1}[0-9]{0,2})?");
				if (!newValue.matches(patron.pattern())) {
					Platform.runLater(() -> {
						pesoAnimal.clear();
					});
				}
			}
		});

		numeroTropa.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				Pattern patron = Pattern.compile("\\b[0-9]+");
				if (!newValue.matches(patron.pattern())) {
					Platform.runLater(() -> {
						numeroTropa.clear();
					});
				}
			}
		});

		numeroGarron.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				Pattern patron = Pattern.compile("\\b[0-9]+");
				if (!newValue.matches(patron.pattern())) {
					Platform.runLater(() -> {
						numeroGarron.clear();
					});
				}
			}
		});

		obtenerSiguienteNroTropaService.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent t) {
				tropaReservada = obtenerSiguienteNroTropaService.getValue();
				numeroTropa.setText(new Integer(tropaReservada.obtenerSiguienteNroDeTropa()).toString());
			}
		});
		
		resetearNumeroTropaService.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent t) {
				if (resetearNumeroTropaService.getValue()) {
					
					Alert alert = new Alert(AlertType.INFORMATION);
					alert.setTitle("Información");
					alert.setHeaderText("Faena Cancelada");
					alert.setContentText("La faena no se ha iniciado");
					alert.showAndWait();
				}
			}
		});

		guardarTropaService.setOnSucceeded(new EventHandler<WorkerStateEvent>() {

			@Override
			public void handle(WorkerStateEvent t) {
				tropaBeanPalcoController = guardarTropaService.getValue();
			}
		});

		obtenerSiguienteGarronService.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
			AnimalBean animalBean = new AnimalBean();

			@Override
			public void handle(WorkerStateEvent t) {
				animalBean = obtenerSiguienteGarronService.getValue();
				numeroGarron.setText(new Integer(animalBean.getGarron()).toString());
			}
		});

		guardarAnimalService.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
			AnimalBean animalBeanGuardado = new AnimalBean();

			@Override
			public void handle(WorkerStateEvent t) {
				animalBeanGuardado = guardarAnimalService.getValue();
				siguienteGarron(numeroGarron);
			}
		});

		cargarCategoriasSegunEspecieService.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent t) {
				categoriasList = cargarCategoriasSegunEspecieService.getValue();
				categoriaContainer.getChildren().clear();
				botonesCategoria.clear();
				for (CategoriaBean categoriaBean : categoriasList) {
					ToggleButton boton = new ToggleButton();
					boton.setToggleGroup(categoriaToggleGroup);
					boton.setText(categoriaBean.getDescripcion());
					boton.setId((new Integer(categoriaBean.getIdCategoria())).toString());
					botonesCategoria.add(boton);
					categoriaContainer.getChildren().add(boton);
				}
				((ToggleButton) (categoriaContainer.getChildren().get(0))).setSelected(true);
			}
		});
		verificarTropaFaenadaService.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent t) {
				if (verificarTropaFaenadaService.getValue()) {
					inicializarFaenaButton.setDisable(true);
					Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("Error Dialog");
					alert.setHeaderText("Numero de tropa Incorrecto");
					alert.setContentText("La tropa " + numeroTropa.getText() + " ya fue faenada o esta "
							+ "ingresando un numero de tropa fuera del rango de tropas reservadas para esa procedencia");
					alert.showAndWait();
				}
			}
		});

		verificarNumeroGarronModificadoService.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent t) {
				if (!verificarNumeroGarronModificadoService.getValue()) {
					imprimirEtiqueta.setDisable(true);
					Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("Error Dialog");
					alert.setHeaderText("Numero de garron Incorrecto");
					alert.setContentText(
							"El garron " + numeroGarron.getText() + " ya fue utilizado en el dia de la fecha");
					alert.showAndWait();
				}
			}
		});

		obtenerSiguienteNroTropaService.setExecutor(executorService);
		guardarTropaService.setExecutor(executorService);
		obtenerSiguienteGarronService.setExecutor(executorService);
		guardarAnimalService.setExecutor(executorService);
		cargarCategoriasSegunEspecieService.setExecutor(executorService);
		verificarTropaFaenadaService.setExecutor(executorService);
		verificarNumeroGarronModificadoService.setExecutor(executorService);
		resetearNumeroTropaService.setExecutor(executorService);

		segundoPanel.setDisable(true);
		tercerPanel.setDisable(true);

		// Cargo Combos
		cargarComboEspecie();
		cargarComboProcedencia();

		// Inicio Radio buttoms
		rbEntera.setToggleGroup(cabeza);
		rbEntera.setSelected(true);
		rbAlMedio.setToggleGroup(cabeza);

		numeroTropa.focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue,
					Boolean newPropertyValue) {
				if (newPropertyValue) {
					inicializarFaenaButton.setDisable(false);
				} else {
					verificarTropaFaenada();

				}
			}
		});

		numeroGarron.focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue,
					Boolean newPropertyValue) {
				if (newPropertyValue) {
					imprimirEtiqueta.setDisable(false);
				} else {
					verificarNumeroGarronModificado();

				}
			}
		});
	}

	/**
	 * Is called by the main application to give a reference back to itself.
	 * 
	 * @param mainApp
	 */
	public void setMainApp(AplicacionPrincipalPalco aplicacionPrincipalPalco) {
		this.aplicacionPrincipalPalco = aplicacionPrincipalPalco;
	}

	@FXML
	private void handleCambiaComboProcedencia() {
		calcularSiguienteNroTropa();
	}

	@FXML
	private void handleInicializarFaena() {
		garronesReservados.clear();

		EspecieBean selectedEspecieBean = (EspecieBean) especie.getSelectionModel().getSelectedItem();
		ProcedenciaBean selectedProcedenciaBean = (ProcedenciaBean) procedencia.getSelectionModel().getSelectedItem();

		if ((selectedEspecieBean != null) && (selectedProcedenciaBean != null)) {
			tropaBeanPalcoController.setEspecieId(selectedEspecieBean.getIdEspecie());
			tropaBeanPalcoController.setProcendeciaId(selectedProcedenciaBean.getIdProcedencia());

			// TODO esto es chamuyo ARREGLAR!!!!
			tropaBeanPalcoController.setEstablecimientoId(1);
			tropaBeanPalcoController.setNumeroTropa(Integer.parseInt(numeroTropa.getText()));

			tropaBeanPalcoController.setAnimales(new ArrayList<Integer>());

			// Guardar tropa te devuelve la tropa guardada con la fecha faena y
			// el id tropa
			guardarTropa(tropaBeanPalcoController);

		} else {
			// Nothing selected.
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error Dialog");
			alert.setHeaderText("No selecciono especie");
			alert.setContentText("Por favor seleccione una especie valida");

			alert.showAndWait();
		}

		primerPanel.setDisable(true);
		segundoPanel.setDisable(false);
		tercerPanel.setDisable(false);
		siguienteGarron(numeroGarron);
	}

	@FXML
	private void handleGuardarCategoria(ActionEvent event) {
		if (event.getSource() instanceof ToggleButton) {
			ToggleButton clickedBtn = (ToggleButton) event.getTarget();
			clickedBtn.setSelected(true);
		}
	}

	@FXML
	private void handleImprimirEtiqueta() {

		int idCategoria = 1;
		for (ToggleButton toggleButton : botonesCategoria) {
			if (toggleButton.isSelected()) {
				idCategoria = Integer.parseInt(toggleButton.getId());
				break;
			}
		}

		if (!pesoAnimal.getText().isEmpty()) {
			Double peso = Double.parseDouble(pesoAnimal.getText().replace(",", "."));

			boolean cabezaAnimalEntera = ((RadioButton) cabeza.getSelectedToggle()).getText()
					.equalsIgnoreCase("Entera");

			int garron = Integer.parseInt(numeroGarron.getText());

			AnimalBean animalBeanAGuardar = new AnimalBean();
			animalBeanAGuardar.setCabezaFaenadaEntera(cabezaAnimalEntera);
			animalBeanAGuardar.setGarron(garron);
			animalBeanAGuardar.setIdCategoria(idCategoria);
			animalBeanAGuardar.setPeso(peso);
			animalBeanAGuardar.setIdTropa(tropaBeanPalcoController.getIdTropa());
			guardarAnimal(animalBeanAGuardar);

			tropaBeanPalcoController.agregarAnimal(animalBeanAGuardar.getIdAnimal());
			pesoAnimal.setText("");

			Etiqueta etiqueta = new Etiqueta();
			etiqueta.imprimirEtiquetas(tropaBeanPalcoController, animalBeanAGuardar);
		}

		else {
			// Nothing selected.
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Mensaje de Error");
			alert.setHeaderText("No ingreso peso");
			alert.setContentText("Por favor ingrese el peso");

			alert.showAndWait();
		}
	}

	@FXML
	private void handleReservarGarron() {

		int idCategoria = 1;
		for (ToggleButton toggleButton : botonesCategoria) {
			if (toggleButton.isSelected()) {
				idCategoria = Integer.parseInt(toggleButton.getId());
				break;
			}
		}

		Double peso = 0.0;
		if (!pesoAnimal.getText().isEmpty()) {
			peso = Double.parseDouble(pesoAnimal.getText().replace(",", "."));
		}

		boolean cabezaAnimalEntera = ((RadioButton) cabeza.getSelectedToggle()).getText().equalsIgnoreCase("Entera");

		int garron = Integer.parseInt(numeroGarron.getText());

		AnimalBean animalBeanAGuardar = new AnimalBean();
		animalBeanAGuardar.setCabezaFaenadaEntera(cabezaAnimalEntera);
		animalBeanAGuardar.setGarron(garron);
		animalBeanAGuardar.setIdCategoria(idCategoria);
		animalBeanAGuardar.setPeso(peso);
		animalBeanAGuardar.setIdTropa(tropaBeanPalcoController.getIdTropa());
		guardarAnimal(animalBeanAGuardar);
		pesoAnimal.setText("");

		garronesReservados.add(garron);
	}

	@FXML
	private void handleFinalizarFaena() {

		if (tropaBeanPalcoController.getAnimales().size() == 0) {

			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Tropa VACÍA");
			alert.setHeaderText("Se abortará la faena de la tropa actual.");
			alert.setContentText("¿Desea continuar?");

			Optional<ButtonType> result = alert.showAndWait();
			if (result.get() == ButtonType.OK) {
				resetearNroTropa();
				/*
				 * Integer garronAnterior =
				 * Integer.parseInt(numeroTropa.getText());
				 * numeroTropa.setText(garronAnterior.toString());
				 */
				tropaBeanPalcoController = new TropaBean();
				numeroGarron.setText("");
				pesoAnimal.setText("");
				segundoPanel.setDisable(true);
				primerPanel.setDisable(false);
				tercerPanel.setDisable(true);

			}

		} else {
			if (!garronesReservados.isEmpty()) {
				Joiner joiner = Joiner.on(", ");

				Alert alert = new Alert(AlertType.CONFIRMATION);
				alert.setTitle("Garrones Incompletos");
				alert.setHeaderText("Los garrones: " + joiner.join(garronesReservados) + " han quedado sin faenar.");

				alert.setContentText("¿Desea faenarlos ahora?");
				Optional<ButtonType> result = alert.showAndWait();

				if (result.get() == ButtonType.OK) {
					numeroGarron.setText(garronesReservados.remove(0).toString());
				} else {
					tropaBeanPalcoController = new TropaBean();
					numeroGarron.setText("");
					pesoAnimal.setText("");
					primerPanel.setDisable(false);
					segundoPanel.setDisable(true);
					tercerPanel.setDisable(true);

					calcularSiguienteNroTropa();
				}
			} else {
				tropaBeanPalcoController = new TropaBean();
				numeroGarron.setText("");
				pesoAnimal.setText("");
				primerPanel.setDisable(false);
				segundoPanel.setDisable(true);
				tercerPanel.setDisable(true);

				calcularSiguienteNroTropa();
			}
		}
	}

	@FXML
	private void handleCargarCategoriasPorEspecie() {
		cargarCategoriasPorEspecies();
	}

	// ******************
	// METODOS PRIVADOS QUE INVOCAN SERVICIOS O TASK
	// ******************

	private void cargarComboEspecie() {
		executorService.submit(fetchListEspecies);
		fetchListEspecies.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent t) {
				especieList = FXCollections.observableArrayList(fetchListEspecies.getValue());

				especie.setItems(especieList);
				for (EspecieBean e : especieList) {
					if (e.getDescripcion().equalsIgnoreCase("Porcinos")) {
						especie.setValue(e);
						return;
					}
				}
			}
		});
	}

	private void cargarComboProcedencia() {
		executorService.submit(fetchListProcedencias);
		fetchListProcedencias.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent t) {
				procedenciaList = FXCollections.observableArrayList(fetchListProcedencias.getValue());

				procedencia.setItems(procedenciaList);
				for (ProcedenciaBean e : procedenciaList) {
					if (e.getDescripcion().equalsIgnoreCase("Estancias")) {
						procedencia.setValue(e);
						return;
					}
				}
				calcularSiguienteNroTropa();
			}
		});
	}

	private void cargarCategoriasPorEspecies() {
		cargarCategoriasSegunEspecieService.setIdEspecie(especie.getValue().getIdEspecie());
		if (cargarCategoriasSegunEspecieService.getState() == State.READY) {
			cargarCategoriasSegunEspecieService.start();
		} else {
			if (cargarCategoriasSegunEspecieService.getState() == State.SUCCEEDED) {
				cargarCategoriasSegunEspecieService.restart();
			}
		}
	}

	private void calcularSiguienteNroTropa() {
		obtenerSiguienteNroTropaService.setProcedenciaBean(procedencia);
		if (obtenerSiguienteNroTropaService.getState() == State.READY) {
			obtenerSiguienteNroTropaService.start();
		} else {
			if (obtenerSiguienteNroTropaService.getState() == State.SUCCEEDED) {
				obtenerSiguienteNroTropaService.restart();
			}
		}
	}
	
	private void resetearNroTropa() {
		resetearNumeroTropaService.setProcedenciaBean(procedencia);
		if (resetearNumeroTropaService.getState() == State.READY) {
			resetearNumeroTropaService.start();
		} else {
			if (resetearNumeroTropaService.getState() == State.SUCCEEDED) {
				resetearNumeroTropaService.restart();
			}
		}
	}

	private void guardarTropa(TropaBean tropaBeanAGuardar) {

		guardarTropaService.setTropaBean(tropaBeanAGuardar);
		if (guardarTropaService.getState() == State.READY) {
			guardarTropaService.start();

		} else {
			if (guardarTropaService.getState() == State.SUCCEEDED) {
				guardarTropaService.restart();
			}
		}
	}

	private void siguienteGarron(TextField numeroGarron) {
		if (obtenerSiguienteGarronService.getState() == State.READY) {
			obtenerSiguienteGarronService.start();
		} else {
			if (obtenerSiguienteGarronService.getState() == State.SUCCEEDED) {
				obtenerSiguienteGarronService.restart();
			}
		}
	}

	public void guardarAnimal(AnimalBean animalBeanAGuardar) {
		guardarAnimalService.setAnimalBeanAGuardar(animalBeanAGuardar);
		if (guardarAnimalService.getState() == State.READY) {
			guardarAnimalService.start();

		} else {
			if (guardarAnimalService.getState() == State.SUCCEEDED) {
				guardarAnimalService.restart();
			}
		}
	}

	public void verificarTropaFaenada() {
		if (!numeroTropa.getText().isEmpty()) {
			verificarTropaFaenadaService.setNroTropa(Integer.parseInt(numeroTropa.getText()));
			verificarTropaFaenadaService
					.setIdProcedencia(procedencia.getSelectionModel().getSelectedItem().getIdProcedencia());
			if (verificarTropaFaenadaService.getState() == State.READY) {
				verificarTropaFaenadaService.start();

			} else {
				if (verificarTropaFaenadaService.getState() == State.SUCCEEDED) {
					verificarTropaFaenadaService.restart();
				}
			}
		}
	}

	public void verificarNumeroGarronModificado() {
		if (!numeroGarron.getText().isEmpty()) {
			verificarNumeroGarronModificadoService.setNroGarron(Integer.parseInt(numeroGarron.getText()));

			if (verificarNumeroGarronModificadoService.getState() == State.READY) {
				verificarNumeroGarronModificadoService.start();

			} else {
				if (verificarNumeroGarronModificadoService.getState() == State.SUCCEEDED) {
					verificarNumeroGarronModificadoService.restart();
				}
			}
		}
	}

	// ********************
	// ******TASKS*****
	// ********************
	/**
	 * Task to fetch details from JSONURL
	 * 
	 * @param <V>
	 */
	private Task<List<EspecieBean>> fetchListEspecies = new Task<List<EspecieBean>>() {
		@Override
		protected List<EspecieBean> call() throws Exception {
			List<EspecieBean> list = null;
			try {
				Gson gson = new Gson();
				list = new Gson().fromJson(readUrl(JSON_URL_ESPECIES), new TypeToken<List<EspecieBean>>() {
				}.getType());
			} catch (Exception e) {
				e.printStackTrace();
			}
			return list;
		}
	};

	private Task<List<ProcedenciaBean>> fetchListProcedencias = new Task<List<ProcedenciaBean>>() {
		@Override
		protected List<ProcedenciaBean> call() throws Exception {
			List<ProcedenciaBean> list = null;
			try {
				Gson gson = new Gson();
				list = gson.fromJson(readUrl(JSON_URL_PROCEDENCIAS), new TypeToken<List<ProcedenciaBean>>() {
				}.getType());
			} catch (Exception e) {
				e.printStackTrace();
			}
			return list;
		}
	};

	// ********************
	// ******SERVICIOS*****
	// ********************

	public static class ObtenerSiguienteNroTropaService extends Service<TropaReservada> {
		private ComboBox<ProcedenciaBean> procedenciaBean = null;

		public ComboBox<ProcedenciaBean> getProcedenciaBean() {
			return procedenciaBean;
		}

		public void setProcedenciaBean(ComboBox<ProcedenciaBean> procedenciaBean) {
			this.procedenciaBean = procedenciaBean;
		}

		protected Task<TropaReservada> createTask() {
			return new Task<TropaReservada>() {
				protected TropaReservada call() throws Exception {
					TropaReservada tropaReservada = null;

					int idProcedencia = procedenciaBean.getValue().getIdProcedencia();
					try {
						Gson gson = new Gson();
						tropaReservada = gson.fromJson(readUrl(JSON_URL_SIGUIENTE_NUMERO_TROPA + idProcedencia),
								new TypeToken<TropaReservada>() {
						}.getType());
					} catch (Exception e) {
						e.printStackTrace();
					}
					return tropaReservada;
				}
			};
		}
	}
	
	
	
	public static class ResetearNumeroTropaService extends Service<Boolean> {
		private ComboBox<ProcedenciaBean> procedenciaBean = null;

		public ComboBox<ProcedenciaBean> getProcedenciaBean() {
			return procedenciaBean;
		}

		public void setProcedenciaBean(ComboBox<ProcedenciaBean> procedenciaBean) {
			this.procedenciaBean = procedenciaBean;
		}

		protected Task<Boolean> createTask() {
			return new Task<Boolean>() {
				protected Boolean call() throws Exception {
					Boolean resultado = false;

					int idProcedencia = procedenciaBean.getValue().getIdProcedencia();
					try {
						Gson gson = new Gson();
						JsonObject gson1 = gson.fromJson(
												readUrl(JSON_URL_RESETEAR_NUMERO_TROPA + idProcedencia),
												JsonObject.class);
						return gson1.get("result").getAsBoolean();
					} catch (Exception e) {
						e.printStackTrace();
					}
					return false;
				}
			};
		}
	}
	

	public static class GuardarTropaService extends Service<TropaBean> {
		protected TropaBean tropaBean = null;

		public TropaBean getTropaBean() {
			return tropaBean;
		}

		public void setTropaBean(TropaBean tropaBean) {
			this.tropaBean = tropaBean;
		}

		protected Task<TropaBean> createTask() {
			return new Task<TropaBean>() {
				protected TropaBean call() throws Exception {
					TropaBean tropaBeanResultado = new TropaBean();
					try {
						GsonBuilder gsonBuilder = new GsonBuilder();
						gsonBuilder.setDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
						Gson gson = gsonBuilder.create();
						tropaBeanResultado = gson.fromJson(writeUrl(JSON_URL_GUARDAR_TROPA, tropaBean),
								new TypeToken<TropaBean>() {
						}.getType());
					} catch (Exception e) {
						e.printStackTrace();
					}
					GuardarTropaService.this.setTropaBean(tropaBeanResultado);
					return tropaBeanResultado;
				}
			};
		}
	}

	public static class ObtenerSiguienteGarronService extends Service<AnimalBean> {

		protected Task<AnimalBean> createTask() {
			return new Task<AnimalBean>() {
				protected AnimalBean call() throws Exception {
					AnimalBean animalBean = new AnimalBean();
					try {
						Gson gson = new Gson();
						animalBean = gson.fromJson(readUrl(JSON_URL_OBTENER_GARRON), new TypeToken<AnimalBean>() {
						}.getType());
					} catch (Exception e) {
						e.printStackTrace();
					}
					return animalBean;
				}
			};
		}

	}

	public static class GuardarAnimalService extends Service<AnimalBean> {

		protected AnimalBean animalBeanAGuardar = null;

		public AnimalBean getAnimalBeanAGuardar() {
			return this.animalBeanAGuardar;
		}

		public void setAnimalBeanAGuardar(AnimalBean animalBean) {
			this.animalBeanAGuardar = animalBean;
		}

		protected Task<AnimalBean> createTask() {
			return new Task<AnimalBean>() {
				@Override
				protected AnimalBean call() throws Exception {
					AnimalBean animalBeanResultado = new AnimalBean();
					try {
						Gson gson = new Gson();
						animalBeanResultado = gson.fromJson(writeUrl(JSON_URL_GUARDAR_ANIMAL, getAnimalBeanAGuardar()),
								new TypeToken<AnimalBean>() {
						}.getType());
					} catch (Exception e) {
						e.printStackTrace();
					}
					return animalBeanResultado;
				}
			};
		}
	}

	public static class CargarCategoriasSegunEspecie extends Service<List<CategoriaBean>> {
		protected int idEspecie;

		public int getIdEspecie() {
			return idEspecie;
		}

		public void setIdEspecie(int idEspecie) {
			this.idEspecie = idEspecie;
		}

		protected Task<List<CategoriaBean>> createTask() {
			return new Task<List<CategoriaBean>>() {
				@Override
				protected List<CategoriaBean> call() throws Exception {
					List<CategoriaBean> list = null;
					try {
						Gson gson = new Gson();
						list = gson.fromJson(readUrl(JSON_URL_CATEGORIAS + idEspecie),
								new TypeToken<List<CategoriaBean>>() {
						}.getType());
					} catch (Exception e) {
						e.printStackTrace();
					}
					return list;
				}
			};
		}
	}

	public static class VerificarTropaFaenadaService extends Service<Boolean> {
		protected int nroTropa;
		protected int idProcedencia;

		public int getNroTropa() {
			return nroTropa;
		}

		public void setNroTropa(int nroTropa) {
			this.nroTropa = nroTropa;
		}

		public int getIdProcedencia() {
			return idProcedencia;
		}

		public void setIdProcedencia(int idProcedencia) {
			this.idProcedencia = idProcedencia;
		}

		protected Task<Boolean> createTask() {
			return new Task<Boolean>() {
				protected Boolean call() throws Exception {

					try {
						Gson gson = new Gson();
						JsonObject gson1 = gson.fromJson(
								readUrl(JSON_URL_VERIFICAR_NUMERO_TROPA + getNroTropa() + "/" + getIdProcedencia()),
								JsonObject.class);
						return gson1.get("result").getAsBoolean();
					} catch (Exception e) {
						e.printStackTrace();
					}
					return false;
				}
			};
		}
	}

	public static class VerificarNumeroGarronModificadoService extends Service<Boolean> {
		protected int nroGarron;

		public int getNroGarron() {
			return nroGarron;
		}

		public void setNroGarron(int nroGarron) {
			this.nroGarron = nroGarron;
		}

		protected Task<Boolean> createTask() {
			return new Task<Boolean>() {
				protected Boolean call() throws Exception {
					try {
						Gson gson = new Gson();
						JsonObject gson1 = gson.fromJson(
								readUrl(JSON_URL_VERIFICAR_NUMERO_GARRON_MODIFICADO + getNroGarron()),
								JsonObject.class);
						return gson1.get("result").getAsBoolean();

					} catch (Exception e) {
						e.printStackTrace();
					}

					return false;
				}
			};
		}
	}

	// *********************
	// GET Y POST, read y write url
	// *********************
	/**
	 * Read the URL and return the json data
	 * 
	 * @param urlString
	 * @return
	 * @throws Exception
	 */
	private static String readUrl(String urlString) throws Exception {
		BufferedReader reader = null;
		try {
			URL url = new URL(urlString);
			reader = new BufferedReader(new InputStreamReader(url.openStream()));
			StringBuffer buffer = new StringBuffer();
			int read;
			char[] chars = new char[1024];
			while ((read = reader.read(chars)) != -1)
				buffer.append(chars, 0, read);
			return buffer.toString();
		} finally {
			if (reader != null)
				reader.close();
		}
	}

	// HTTP POST request
	private static String writeUrl(String url, Object data) throws Exception {

		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// add reuqest header
		con.setRequestMethod("POST");
		con.setRequestProperty("Content-Type", "application/json");
		// con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

		// String urlParameters =
		// "sn=C02G8416DRJM&cn=&locale=&caller=&num=12345";

		// Send post request
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		// wr.writeBytes(urlParameters);
		wr.writeChars(data.toString());
		wr.flush();
		wr.close();

		int responseCode = con.getResponseCode();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
			StringBuffer buffer = new StringBuffer();
			int read;
			char[] chars = new char[1024];
			while ((read = reader.read(chars)) != -1)
				buffer.append(chars, 0, read);

			String tropa = buffer.toString();
			return tropa;

		} finally {
			if (reader != null)
				reader.close();
		}

	}

}

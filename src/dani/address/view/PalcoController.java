package dani.address.view;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

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
	

	private ExecutorService executorService = Executors.newCachedThreadPool();

	private ObtenerSiguienteNroTropaService obtenerSiguienteNroTropaService = new ObtenerSiguienteNroTropaService();
	private CargarCategoriasSegunEspecie cargarCategoriasSegunEspecieService = new CargarCategoriasSegunEspecie();
	private GuardarTropaService guardarTropaService = new GuardarTropaService();
	private ObtenerSiguienteGarronService obtenerSiguienteGarronService = new ObtenerSiguienteGarronService();
	private GuardarAnimalService guardarAnimalService = new GuardarAnimalService();
	private VerificarTropaFaenadaService verificarTropaFaenadaService = new VerificarTropaFaenadaService();
	private VerificarNumeroGarronModificadoService verificarNumeroGarronModificadoService = new VerificarNumeroGarronModificadoService();
	

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

		segundoPanel.setDisable(true);
		tercerPanel.setDisable(true);

		// Cargo Combos
		cargarComboEspecie();
		cargarComboProcedencia();

		// Inicio Radio buttoms
		rbEntera.setToggleGroup(cabeza);
		rbEntera.setSelected(true);
		rbAlMedio.setToggleGroup(cabeza);

		obtenerSiguienteNroTropaService.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent t) {
				System.out.println("Cambie procedencia de nuevo!!!!");
				tropaReservada = obtenerSiguienteNroTropaService.getValue();
				System.out.println("Tropa reservada en el handle del calcularNumeroTropa" + tropaReservada);
				System.out.println(tropaReservada.getUltimaTropa());
				numeroTropa.setText(new Integer(tropaReservada.obtenerSiguienteNroDeTropa()).toString());
			}
		});

		guardarTropaService.setOnSucceeded(new EventHandler<WorkerStateEvent>() {

			@Override
			public void handle(WorkerStateEvent t) {
				tropaBeanPalcoController = guardarTropaService.getValue();
				System.out.println("Impresion de tropaBean en el handle guardar tropa en el setOnSucceeded: "
						+ tropaBeanPalcoController);
			}
		});

		obtenerSiguienteGarronService.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
			AnimalBean animalBean = new AnimalBean();

			@Override
			public void handle(WorkerStateEvent t) {
				System.out.println("Obtener garron de nuevo!!!!");
				animalBean = obtenerSiguienteGarronService.getValue();
				System.out.println("Animal bean con el siguiente numero de garron: " + animalBean);
				System.out.println(animalBean.getGarron());
				numeroGarron.setText(new Integer(animalBean.getGarron()).toString());
			}
		});

		guardarAnimalService.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
			AnimalBean animalBeanGuardado = new AnimalBean();

			@Override
			public void handle(WorkerStateEvent t) {
				animalBeanGuardado = guardarAnimalService.getValue();
				System.out.println(
						"Invocando al handle del guardarAnimalService on succeeded method: " + animalBeanGuardado);
				System.out.println("el estado del servicio, que poronga es: " + guardarAnimalService.getState());
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
				((ToggleButton)(categoriaContainer.getChildren().get(0))).setSelected(true);
			}
		});
		verificarTropaFaenadaService.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent t) {
				if (verificarTropaFaenadaService.getValue()) {
					inicializarFaenaButton.setDisable(true);
					System.out.println("supuestamente deshabilite el boton");
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
					System.out.println("supuestamente deshabilite el boton");
					imprimirEtiqueta.setDisable(true);
					Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("Error Dialog");
					alert.setHeaderText("Numero de garron Incorrecto");
					alert.setContentText("El garron " + numeroGarron.getText() + " ya fue utilizado en el dia de la fecha");
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

		numeroTropa.focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue,
					Boolean newPropertyValue) {
				if (newPropertyValue) {
					System.out.println("Textfield on focus");
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
					System.out.println("Textfield on focus");
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
		EspecieBean selectedEspecieBean = (EspecieBean) especie.getSelectionModel().getSelectedItem();
		ProcedenciaBean selectedProcedenciaBean = (ProcedenciaBean) procedencia.getSelectionModel().getSelectedItem();

		if ((selectedEspecieBean != null) && (selectedProcedenciaBean != null)) {
			tropaBeanPalcoController.setEspecieId(selectedEspecieBean.getIdEspecie());
			tropaBeanPalcoController.setProcendeciaId(selectedProcedenciaBean.getIdProcedencia());

			// TODO esto es chamuyo ARREGLAR!!!!
			tropaBeanPalcoController.setEstablecimientoId(1);
			tropaBeanPalcoController.setNumeroTropa(Integer.parseInt(numeroTropa.getText()));

			System.out.println("especie id: " + selectedEspecieBean.getIdEspecie());
			System.out.println("procedencia id: " + selectedProcedenciaBean.getIdProcedencia());
			System.out.println(procedencia.getSelectionModel().getSelectedItem());
			System.out.println("La tropa bean como json: " + tropaBeanPalcoController.toString());

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
		System.out.println("Se terino de ejecutar el servicio????::: " + executorService.isTerminated());
	}

	@FXML
	private void handleGuardarCategoria(ActionEvent event) {
		if (event.getSource() instanceof ToggleButton) {
			ToggleButton clickedBtn = (ToggleButton) event.getTarget();
			System.out.println(clickedBtn.getText());
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

		System.out.println("El peso del animal esssssss " + pesoAnimal.getText());

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
			pesoAnimal.setText("");

			Etiqueta etiqueta = new Etiqueta();
			System.out.println("Imprimiendo tropaBeanPalcoController " + tropaBeanPalcoController);
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

		// TODO: falta el procedimiento de imprimir etiqueta en si. (el pelpa en
		// el bicho tomuer)

		/*
		 * segundoPanel.setDisable(true); tercerPanel.setDisable(false);
		 */

		System.out.println("por buscar el siguiente garron deberia aprecer despues de esperar");
	}

	@FXML
	private void handleFinalizarFaena() {
		System.out.println("Estoy ejecutando el handle finalizar faena");
		tropaBeanPalcoController = new TropaBean();
		numeroGarron.setText("");

		segundoPanel.setDisable(true);
		primerPanel.setDisable(false);

		calcularSiguienteNroTropa();
		tercerPanel.setDisable(true);

	}

	@FXML
	private void handleCargarCategoriasPorEspecie() {
		System.out.println("FUNCIONA LA CARGADA de categorias segun especie");
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
				System.out.println(especieList);

				especie.setItems(especieList);
				System.out.println("Tamaño comboooooo" + especieList.size());
				for (EspecieBean e : especieList) {
					System.out.println("Cargando comboooo" + e.getDescripcion());
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
				System.out.println(procedenciaList);

				System.out.println("Lista de procedencias =======");
				for (ProcedenciaBean procedenciaBean : procedenciaList) {
					System.out.println("Procedencia: " + procedenciaBean.getIdProcedencia() + " - "
							+ procedenciaBean.getDescripcion());
				}
				procedencia.setItems(procedenciaList);
				System.out.println("Tamaï¿½o comboooooo" + procedenciaList.size());
				for (ProcedenciaBean e : procedenciaList) {
					System.out.println("Cargando comboooo" + e.getDescripcion());
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
				System.out.println("Entre al if del succeeded de obtener siguiente garron: "
						+ obtenerSiguienteGarronService.getState());
				obtenerSiguienteGarronService.restart();
			}
		}
	}

	public void guardarAnimal(AnimalBean animalBeanAGuardar) {
		guardarAnimalService.setAnimalBeanAGuardar(animalBeanAGuardar);
		if (guardarAnimalService.getState() == State.READY) {
			System.out.println("antes del estado start: " + guardarAnimalService.getState());
			guardarAnimalService.start();
			System.out.println("despues del estado start: " + guardarAnimalService.getState());

		} else {
			if (guardarAnimalService.getState() == State.SUCCEEDED) {
				System.out.println("Entre al if del succeeded de guardar animal: " + guardarAnimalService.getState());
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
					System.out.println("Entre al task cuando cambio procedenciaaaaaaa");
					TropaReservada tropaReservada = null;

					System.out.println("Procedencia combo boxxxxxx" + procedenciaBean.getValue());
					int idProcedencia = procedenciaBean.getValue().getIdProcedencia();
					System.out.println("Parametro al readUrl: " + JSON_URL_SIGUIENTE_NUMERO_TROPA + idProcedencia);
					System.out.println("Entrando en el Task fetchNumeroTropa");
					try {
						Gson gson = new Gson();
						tropaReservada = gson.fromJson(readUrl(JSON_URL_SIGUIENTE_NUMERO_TROPA + idProcedencia),
								new TypeToken<TropaReservada>() {
						}.getType());
					} catch (Exception e) {
						e.printStackTrace();
					}
					System.out.println("Tropa reservada de Taskkkkkk" + tropaReservada);
					return tropaReservada;
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
					System.out.println("el resultado de guardar la tropa nueva: " + tropaBeanResultado);
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
					System.out.println("Obtener siguiente garron: " + animalBean);
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
					System.out.println("Obtengo el animal bean guardado: " + animalBeanResultado);
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
					System.out.println("Entre al task cuando verifico numero tropa");

					try {
						Gson gson = new Gson();
						JsonObject gson1 = gson.fromJson(
								readUrl(JSON_URL_VERIFICAR_NUMERO_TROPA + getNroTropa() + "/" + getIdProcedencia()),
								JsonObject.class);

						System.out.println("======================");
						System.out.println("viendo si devolvemos un booleano" + gson1.get("result").getAsBoolean());
						System.out.println("======================");
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
					System.out.println("Entre al task cuando verifico garron modificado");

					try {
						Gson gson = new Gson();
						JsonObject gson1 = gson.fromJson(
								readUrl(JSON_URL_VERIFICAR_NUMERO_GARRON_MODIFICADO + getNroGarron()),
								JsonObject.class);

						System.out.println("======================");
						System.out.println("viendo si devolvemos un booleano" + gson1.get("result").getAsBoolean());
						System.out.println("======================");
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
		System.out.println("url " + urlString);
		try {
			URL url = new URL(urlString);
			reader = new BufferedReader(new InputStreamReader(url.openStream()));
			StringBuffer buffer = new StringBuffer();
			int read;
			char[] chars = new char[1024];
			while ((read = reader.read(chars)) != -1)
				buffer.append(chars, 0, read);

			System.out.println("Estoy en el readUrl devolviendo " + buffer.toString());
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
		// System.out.println("\nSending 'POST' request to URL : " + url);
		// System.out.println("Post parameters : " + urlParameters);
		System.out.println("Response Code : " + responseCode);

		BufferedReader reader = null;
		try {
			System.out.println();
			reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
			StringBuffer buffer = new StringBuffer();
			int read;
			char[] chars = new char[1024];
			while ((read = reader.read(chars)) != -1)
				buffer.append(chars, 0, read);

			String tropa = buffer.toString();
			System.out.println("metodo writeurl despues de haber guardado: " + tropa);
			return tropa;

		} finally {
			if (reader != null)
				reader.close();
		}

	}

}

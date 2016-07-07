package dani.address.view;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import dani.address.AplicacionPrincipalPalco;
import bean.especie.EspecieBean;
import bean.procedencia.ProcedenciaBean;
import bean.tropa.AnimalBean;
import bean.tropa.TropaBean;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import tropa.TropaReservada;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;

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
    private ToggleButton caponButton;
    
    @FXML
    private ToggleButton chanchaButton;
    
    @FXML
    private ToggleButton chanchoButton;
    
    
    // Reference to the main application.
    private AplicacionPrincipalPalco aplicacionPrincipalPalco;
    
	private ObservableList<EspecieBean> especieList;
	private ObservableList<ProcedenciaBean> procedenciaList;
	private TropaReservada tropaReservada;
	private static final String JSON_URL_ESPECIES = "http://localhost:8080/frigorifico/rest/especies";
	private static final String JSON_URL_PROCEDENCIAS = "http://localhost:8080/frigorifico/rest/procedencias";
	private static final String JSON_URL_SIGUIENTE_NUMERO_TROPA  = "http://localhost:8080/frigorifico/rest/siguiente_tropa/";
	private static final String JSON_URL_GUARDAR_TROPA  = "http://localhost:8080/frigorifico/rest/nueva_tropa_en_palco";
	private static final String JSON_URL_GUARDAR_ANIMAL  = "http://localhost:8080/frigorifico/rest/agregar_animal_a_tropa";
	private static final String JSON_URL_OBTENER_GARRON  = "http://localhost:8080/frigorifico/rest/obtener_siguiente_garron";

	private ExecutorService executorService = Executors.newCachedThreadPool();
	
	private TropaBean tropaBean = new TropaBean();
	private AnimalBean animalBeanAGuardar = new AnimalBean();
	
	
	final ToggleGroup cabeza = new ToggleGroup();
	final ToggleGroup categoriaButtons = new ToggleGroup();
	
	/**
     * The constructor.
     * The constructor is called before the initialize() method.
     */
    public PalcoController() {
    }

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
    	
    	segundoPanel.setDisable(true);
    	tercerPanel.setDisable(true);
    	
    	//Cargo Combos
    	cargarComboEspecie();
    	cargarComboProcedencia();
      	
    	//Inicio Radio buttoms
    	rbEntera.setToggleGroup(cabeza);
    	rbEntera.setSelected(true);
    	rbAlMedio.setToggleGroup(cabeza);
    	
    	//Cargando numero de tropa
    	calcularNumeroTropa();
    	
    	//inicio categoriaButtoms
    	caponButton.setToggleGroup(categoriaButtons);
    	chanchaButton.setToggleGroup(categoriaButtons);
    	chanchoButton.setToggleGroup(categoriaButtons);
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
    private void handleGuardarTropa() {
        EspecieBean selectedEspecieBean = (EspecieBean) especie.getSelectionModel().getSelectedItem();
        ProcedenciaBean selectedProcedenciaBean = (ProcedenciaBean) procedencia.getSelectionModel().getSelectedItem();
        
        if ((selectedEspecieBean != null) && (selectedProcedenciaBean != null)){
        	
        	tropaBean.setEspecieId(selectedEspecieBean.getIdEspecie());
        	tropaBean.setProcendeciaId(selectedProcedenciaBean.getIdProcedencia());
        	tropaBean.setAnimalesRecibidos(140);
        	tropaBean.setEstablecimientoId(1);
        	System.out.println("especie id: " + selectedEspecieBean.getIdEspecie());
        	System.out.println("procedencia id: " + selectedProcedenciaBean.getIdProcedencia());
        	System.out.println(procedencia.getSelectionModel().getSelectedItem());
        	
        	System.out.println(tropaBean);
        	
        	executorService.submit(guardarTropaBean);
        	guardarTropaBean.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
    			@Override
    			public void handle(WorkerStateEvent t) {
    				guardarTropaBean.getValue();
    				System.out.println(tropaBean);
    			}
    		});


        } else {
            // Nothing selected.
           	Alert alert = new Alert(AlertType.ERROR);
        	alert.setTitle("Error Dialog");
        	alert.setHeaderText("No selecciono especie");
        	alert.setContentText("Por favor seleccione una especie valida");

        	alert.showAndWait();
        }
        
        //System.out.println("Id primer Panelllllllll" + primerPanel.getId());
        primerPanel.setDisable(true);
        segundoPanel.setDisable(false);
        
        executorService.submit(obtenerSiguienteGarron);
        obtenerSiguienteGarron.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
        	AnimalBean animalBean = new AnimalBean();
			@Override
			public void handle(WorkerStateEvent t) {
		    	System.out.println("Cambie procedencia de nuevo!!!!");
				animalBean = obtenerSiguienteGarron.getValue();
				System.out.println(animalBean);
				System.out.println(animalBean.getGarron());
				numeroGarron.setText(new Integer(animalBean.getGarron()).toString());
			}
		});
    }
    
   
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
		private String writeUrl(String url, Object data) throws Exception {

			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			//add reuqest header
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "application/json");
			//con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

			//String urlParameters = "sn=C02G8416DRJM&cn=&locale=&caller=&num=12345";
			
			// Send post request
			con.setDoOutput(true);			
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			//wr.writeBytes(urlParameters);
			wr.writeChars(data.toString());
			wr.flush();
			wr.close();

			int responseCode = con.getResponseCode();
			//System.out.println("\nSending 'POST' request to URL : " + url);
			//System.out.println("Post parameters : " + urlParameters);
			System.out.println("Response Code : " + responseCode);

			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
				StringBuffer buffer = new StringBuffer();
				int read;
				char[] chars = new char[1024];
				while ((read = reader.read(chars)) != -1)
					buffer.append(chars, 0, read);
				
				String tropa = buffer.toString();
				System.out.println(tropa);
				return tropa;
				
			} finally {
				if (reader != null)
					reader.close();
			}
			
		}

	
    
    /**
	 * Task to fetch details from JSONURL
	 * 
	 * @param <V>
	 */
	private Task<List<EspecieBean>> fetchList = new Task<List<EspecieBean>>() {
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
	
	private Task<List<ProcedenciaBean>> fetchListProcedencia = new Task<List<ProcedenciaBean>>() {
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
	
	private Task<TropaReservada> fetchNumeroTropa = new Task<TropaReservada>() {
		@Override
		protected TropaReservada call() throws Exception {
			System.out.println("Entre al task cuando cambio procedenciaaaaaaa");
			TropaReservada tropaReservada = null;
			while(procedencia.getValue() == null ){
				//TODO: SOlucionar esta mierda del while... sincronizar los hilos
			}
			System.out.println("Procedencia combo boxxxxxx" + procedencia.getValue());
			int idProcedencia = procedencia.getValue().getIdProcedencia();
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
	
	
    /**
	 * Task to fetch details from JSONURL
	 * 
	 * @param <V>
	 */
	private Task<TropaBean> guardarTropaBean = new Task<TropaBean>() {
		@Override
		protected TropaBean call() throws Exception {
			TropaBean tropaBeanResultado= new TropaBean();
			try {
				Gson gson = new Gson();
				tropaBeanResultado = gson.fromJson(writeUrl(JSON_URL_GUARDAR_TROPA, tropaBean), new TypeToken<TropaBean>() {
				}.getType());
			} catch (Exception e) {
				e.printStackTrace();
			}
			return tropaBeanResultado;
		}
	};
	
	private Task<AnimalBean> guardarAnimalBean = new Task<AnimalBean>() {
		@Override
		protected AnimalBean call() throws Exception {
			AnimalBean animalBeanResultado= new AnimalBean();
			try {
				Gson gson = new Gson();
				animalBeanResultado = gson.fromJson(writeUrl(JSON_URL_GUARDAR_ANIMAL, animalBeanAGuardar), 
						new TypeToken<AnimalBean>() {
				}.getType());
			} catch (Exception e) {
				e.printStackTrace();
			}
			return animalBeanResultado;
		}
	};
    
	private Task<AnimalBean> obtenerSiguienteGarron = new Task<AnimalBean>() {
		@Override
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
	
	//AGREGO A PARTIR DE ACA PARA PROBAR LO DE SERVICE
	
	public static class FirstLineService extends Service<String> {
        private StringProperty url = new SimpleStringProperty(this, "http://docs.oracle.com/javafx/2/threads/jfxpub-threads.htm");
        public final void setUrl(String value) { url.set(value); }
        public final String getUrl() { return url.get(); }
        public final StringProperty urlProperty() { return url; }

        protected Task createTask() {
            final String _url = getUrl();
            return new Task<String>() {
                protected String call() throws Exception {
                    URL u = new URL(_url);
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(u.openStream()));
                    String result = in.readLine();
                    in.close();
                    return result;
                }
            };
        }
    }
    
	// HASTA ACA
	
	@FXML
	private void pobandoService(){
		executorService.submit(fetchList);
	}	
    @FXML
    private void cargarComboEspecie(){
    	executorService.submit(fetchList);
    	fetchList.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent t) {
				especieList = FXCollections.observableArrayList(fetchList.getValue());
				System.out.println(especieList);
				
				especie.setItems(especieList);
				System.out.println("Tamaño comboooooo" + especieList.size());
		    	for (EspecieBean e: especieList){
		    		System.out.println("Cargando comboooo" + e.getDescripcion());
		    		if(e.getDescripcion().equalsIgnoreCase("Porcinos")){
		    			especie.setValue(e);
		    			return;
		    		}
		    	} 
				
			}
		});

    }
    
    @FXML
    private void cargarComboProcedencia(){
    	executorService.submit(fetchListProcedencia);
    	fetchListProcedencia.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent t) {
				procedenciaList = FXCollections.observableArrayList(fetchListProcedencia.getValue());
				System.out.println(procedenciaList);
				
				System.out.println("Lista de procedencias ======="  );
				for (ProcedenciaBean procedenciaBean : procedenciaList) {
					System.out.println("Procedencia: " + procedenciaBean.getIdProcedencia() + " - " + procedenciaBean.getDescripcion());
				}
				procedencia.setItems(procedenciaList);
				System.out.println("Tamaño comboooooo" + procedenciaList.size());
		    	for (ProcedenciaBean e: procedenciaList){
		    		System.out.println("Cargando comboooo" + e.getDescripcion());
		    		if(e.getDescripcion().equalsIgnoreCase("Estancias")){
		    			procedencia.setValue(e);
		    			return;
		    		}
		    	} 
				
			}
		});

    }
    
    @FXML
    private void calcularNumeroTropa(){
    	System.out.println("Cambie procedencia!!!!");
    	executorService.submit(fetchNumeroTropa);
    	System.out.println("Pase el submit!!!!");
    	fetchNumeroTropa.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent t) {
		    	System.out.println("Cambie procedencia de nuevo!!!!");
				tropaReservada = fetchNumeroTropa.getValue();
				System.out.println(tropaReservada);
				System.out.println(tropaReservada.getUltimaTropa());
				numeroTropa.setText(new Integer(tropaReservada.obtenerSiguienteNroDeTropa()).toString());
			}
		});
    	
    }
    
    @FXML
    private void cambiaComboProcedencia(){
    	 Task<TropaReservada> fetchNumeroTropaNuevo = new Task<TropaReservada>() {
    		@Override
    		protected TropaReservada call() throws Exception {
    			System.out.println("++++++++Entre al task cuando cambio procedenciaaaaaaa");
    			TropaReservada tropaReservada = null;
    			while(procedencia.getValue() == null ){
    				//TODO: SOlucionar esta mierda del while... sincronizar los hilos
    			}
    			System.out.println("+++++++Procedencia combo boxxxxxx" + procedencia.getValue());
    			int idProcedencia = procedencia.getValue().getIdProcedencia();
    			System.out.println("++++++++Parametro al readUrl: " + JSON_URL_SIGUIENTE_NUMERO_TROPA + idProcedencia);
    			System.out.println("+++++++++Entrando en el Task fetchNumeroTropa");
    			try {
    				Gson gson = new Gson();
    				tropaReservada = gson.fromJson(readUrl(JSON_URL_SIGUIENTE_NUMERO_TROPA + idProcedencia), 
    														new TypeToken<TropaReservada>() {
    														}.getType());
    			} catch (Exception e) {
    				e.printStackTrace();
    			}
    			System.out.println("+++++++++++Tropa reservada de Taskkkkkk" + tropaReservada);
    			return tropaReservada;
    		}
    	};
    	
    	ExecutorService executorServiceNuevo = Executors.newCachedThreadPool();
    	executorServiceNuevo.submit(fetchNumeroTropaNuevo);
    	System.out.println("+++++++++++++Pase el submit!!!!");
    	fetchNumeroTropaNuevo.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent t) {
		    	System.out.println("++++++++++++Cambie procedencia de nuevo!!!!");
				tropaReservada = fetchNumeroTropaNuevo.getValue();
				System.out.println(tropaReservada);
				System.out.println(tropaReservada.getUltimaTropa());
				numeroTropa.setText(new Integer(tropaReservada.obtenerSiguienteNroDeTropa()).toString());
			}
		});
    	
    	/*
    	System.out.println("Entre a cambiaComboProcedenciaaaa");
    	if (executorService == null || executorService.isShutdown()){
    		System.out.println("Entre al iffffff del executorService");
    		executorService = Executors.newSingleThreadExecutor();
    	}
    	calcularNumeroTropa();
    	executorService.shutdown();    	
    	*/
    	
    }
    
    @FXML
    private void guardarCategoria(ActionEvent event){
    	if (event.getSource() instanceof ToggleButton) { //should always be true in your example
    		ToggleButton clickedBtn = (ToggleButton) event.getTarget(); // that's the button that was clicked
    	    System.out.println(clickedBtn.getText()); // prints the id of the button
    	    clickedBtn.setSelected(true);
    	   
    	}
    }

    @FXML
    private void imprimirEtiqueta(){
    	int idCategoria = 1;
    	if(caponButton.isPressed()){
    		idCategoria = 1;
    	}
    	if(chanchaButton.isPressed()){
    		idCategoria = 2;
    	}
    	if(chanchoButton.isPressed()){
    		idCategoria = 4;
    	}
    	
    	Double peso = Double.parseDouble(pesoAnimal.getText());
    	boolean cabezaAnimalEntera = ((RadioButton)cabeza.getSelectedToggle()).getText().equalsIgnoreCase("Entera");
    	int garron = Integer.parseInt(numeroGarron.getText());
    	int idTropa = Integer.parseInt(numeroTropa.getText());
    	
    	animalBeanAGuardar.setCabezaFaenadaEntera(cabezaAnimalEntera);
    	animalBeanAGuardar.setGarron(garron);
    	animalBeanAGuardar.setIdCategoria(idCategoria);
    	animalBeanAGuardar.setPeso(peso);
    	animalBeanAGuardar.setIdTropa(idTropa);
    	
    	executorService.submit(guardarAnimalBean);
    	guardarAnimalBean.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
    		@Override
    		public void handle(WorkerStateEvent t) {
    			animalBeanAGuardar = guardarAnimalBean.getValue();
    			System.out.println(animalBeanAGuardar);
    		}
    	});
    	
    	//TODO: falta el procedimiento de imprimir etiqueta en si. (el pelpa en el bicho tomuer) 
    	
    	
    	/*segundoPanel.setDisable(true);
    	tercerPanel.setDisable(false);*/
    	
    	
    	
    	executorService.submit(obtenerSiguienteGarron);
        obtenerSiguienteGarron.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
        	AnimalBean animalBean = new AnimalBean();
			@Override
			public void handle(WorkerStateEvent t) {
		    	System.out.println("Cambie procedencia de nuevo!!!!");
				animalBean = obtenerSiguienteGarron.getValue();
				System.out.println(animalBean);
				System.out.println(animalBean.getGarron());
				numeroGarron.setText(new Integer(animalBean.getGarron()).toString());
			}
		});
    	
    	   	 
    	pesoAnimal.setText("");
    	

    }
    
}

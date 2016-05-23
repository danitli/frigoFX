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
import bean.tropa.TropaBean;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import tropa.TropaReservada;
import javafx.scene.control.Alert.AlertType;

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
    
    // Reference to the main application.
    private AplicacionPrincipalPalco aplicacionPrincipalPalco;
    
	private ObservableList<EspecieBean> especieList;
	private ObservableList<ProcedenciaBean> procedenciaList;
	private static final String JSON_URL_ESPECIES = "http://localhost:8080/frigorifico/rest/especies";
	private static final String JSON_URL_PROCEDENCIAS = "http://localhost:8080/frigorifico/rest/procedencias";
	private static final String JSON_URL_SIGUIENTE_NUMERO_TROPA  = "http://localhost:8080/frigorifico/rest/siguiente_tropa/";
	private static final String JSON_URL_GUARDAR_TROPA  = "http://localhost:8080/frigorifico/rest/nueva_tropa_en_palco";

	private final ExecutorService executorService = Executors.newCachedThreadPool();
	private TropaBean tropaBean = new TropaBean();
	
	final ToggleGroup cabeza = new ToggleGroup();
	
	
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
    	
    	//Cargo Combos
    	cargarComboEspecie();
    	cargarComboProcedencia();
      	
    	//Inicio Radio buttoms
    	rbEntera.setToggleGroup(cabeza);
    	rbEntera.setSelected(true);
    	rbAlMedio.setToggleGroup(cabeza);
    	
    	//Cargando numero de tropa
    	//numeroTropa.setText(siguienteNumeroTropa);
    	
       
    	
         
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
		private String writeUrl(String url) throws Exception {

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
			wr.writeChars(tropaBean.toString());
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
			TropaReservada tropaReservada = null;
			int idProcedencia = procedencia.getValue().getIdProcedencia();
			try {
				Gson gson = new Gson();
				tropaReservada = gson.fromJson(readUrl(JSON_URL_SIGUIENTE_NUMERO_TROPA + idProcedencia), new TypeToken<TropaReservada>() {
				}.getType());
			} catch (Exception e) {
				e.printStackTrace();
			}
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
			
			try {
				Gson gson = new Gson();
				tropaBean = gson.fromJson(writeUrl(JSON_URL_GUARDAR_TROPA), new TypeToken<TropaBean>() {
				}.getType());
			} catch (Exception e) {
				e.printStackTrace();
			}
			return tropaBean;
		}
	};
    
	
	
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
    	executorService.submit(fetchNumeroTropa);
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
    

	
}

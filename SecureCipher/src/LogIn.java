import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Base64;
import javax.crypto.SecretKey;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class LogIn extends Application {
	// use your Database URL user name and password. Works for both cloud and local databases. Just replace your JDBC URL, User name and password below
		String JDBC_URL = "jdbc:mysql://securitydb-2.cjpapks9auhg.ap-southeast-2.rds.amazonaws.com:3306/userdata?user=admin";
		String USERNAME = "admin";
		String PASSWORD = "Bimaya123";
	
	DES desAlgo;
	Connection connection;
	int loggedInUser;
	Stage mainStage;
	Scene mainScene;
	SecretKey masterKey;
	DES des1;
	public void start(Stage primaryStage) throws Exception {
		this.connection = connectToDB();
		if(this.connection == null) {
			System.exit(1);
		}
		
		this.desAlgo = new DES(); 
		desAlgo.setSecretkey(masterKey);
		
		primaryStage.setTitle("LogIn!");
		mainStage = primaryStage;
		masterKey = new SecretKeySpec(Base64.getDecoder().decode("mGu6enCzyBo="), "DES");
		BorderPane root = new BorderPane();
		Image backgrndImage = new Image("/loginimg.jpg");
		ImageView imageView = new ImageView(backgrndImage);
		imageView.setImage(backgrndImage);
		imageView.setFitWidth(1000);
		imageView.setFitHeight(950);
		root.getChildren().add(imageView);

		mainScene = new Scene(root, 1000, 750);

		VBox ccpane = new VBox();
		CaesarCipher(ccpane);
		VBox despane = new VBox();
		DES(despane);
		VBox aespane = new VBox();
		AES(aespane);
		VBox algorithms = new VBox();
		VBox loginVBox = new VBox();
		loginWindow(loginVBox);
		Menu(algorithms, ccpane, despane, aespane, loginVBox);
		root.setCenter(despane);
		root.setLeft(algorithms);

		Scene loginScene = getLoginScene(loginVBox);
		primaryStage.setScene(loginScene);
		primaryStage.show();
	}

	private void loginWindow(VBox pane) {
		Text desc = new Text();
		desc.setText("Enter your credentials");
		desc.setFont(Font.font("Arial", FontWeight.NORMAL, 40));
		desc.setFill(Color.WHITE);

		Label userNameLabel = new Label("User Name : ");
		userNameLabel.setTextFill(Color.WHITESMOKE);
		TextField txtUserName = new TextField();
		txtUserName.setPromptText("User Name : ");
		txtUserName.setPrefSize(40, 20);
		txtUserName.setMaxWidth(200);
		txtUserName.setMaxHeight(40);

		Label passwordLabel = new Label("Password : ");
		passwordLabel.setTextFill(Color.WHITESMOKE);
		PasswordField txtpassword = new PasswordField();
		txtpassword.setPromptText("Password : ");
		txtpassword.setPrefSize(40, 20);
		txtpassword.setMaxWidth(200);
		txtpassword.setMaxHeight(40);

		Button btn = new Button();
		btn.setPrefSize(100, 20);
		btn.setText("LogIn");
		btn.setFont(Font.font("Calibri", FontWeight.BOLD, 20));

		Text loginResultText = new Text();
		loginResultText.setFont(Font.font("Arial", FontWeight.NORMAL, 20));
		loginResultText.setFill(Color.WHITE);

		pane.getChildren().clear();
		pane.setAlignment(Pos.CENTER);
		pane.setPadding(new Insets(25, 25, 25, 25));
		pane.setSpacing(10);
		pane.getChildren().addAll(desc, userNameLabel, txtUserName, passwordLabel, txtpassword, btn, loginResultText);

		btn.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {

				try {

					Statement statement = connection.createStatement();
					String userName = txtUserName.getText();
					String password = txtpassword.getText();
					String sql = "SELECT * FROM userdata, passwords where userdata.id = passwords.ID and " + "UserName = \""
							+ userName + "\" and userdata.password = \"" + password + "\" and " + "Active = 1";
					System.out.println(sql);

					ResultSet resultSet = statement.executeQuery(sql);

					if (resultSet.next()) {
						
						System.out.println("Successful login ");
						loginResultText.setText("Login Successful");
						loggedInUser = resultSet.getInt("userdata.id");
						mainStage.setScene(mainScene);
						

					} else {
						System.out.println("Unsuccessful login ");
						loginResultText.setText("Login Unsuccessful");
					}

					// Close external resources
					resultSet.close();
					statement.close();

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	private void Menu(VBox algorithms, VBox ccpane, VBox despane, VBox aespane, VBox loginVBox) {
		MenuBar menuBar = new MenuBar();
		Menu fileMenu = new Menu("Choose the Algorithm");
		MenuItem DES = new MenuItem("DES");
		MenuItem AES = new MenuItem("AES");
		MenuItem CaesarC = new MenuItem("Caesar Cipher");
		MenuItem exit = new MenuItem("EXIT");
		fileMenu.getItems().addAll(DES, AES, CaesarC, exit);
		menuBar.getMenus().add(fileMenu);

		algorithms.getChildren().clear();
		algorithms.setAlignment(Pos.CENTER);
		algorithms.setPadding(new Insets(25, 25, 25, 25));
		algorithms.setSpacing(10);

		algorithms.getChildren().addAll(menuBar);
		CaesarC.setOnAction(event -> {
			((BorderPane)mainStage.getScene().getRoot()).setCenter(ccpane);
			clearTextFieldsInPane(ccpane);
		});
		DES.setOnAction(event ->  {
			((BorderPane)mainStage.getScene().getRoot()).setCenter(despane);
			clearTextFieldsInPane(despane);
		});
		AES.setOnAction(event -> {
			((BorderPane)mainStage.getScene().getRoot()).setCenter(aespane);
			clearTextFieldsInPane(aespane);
		});
		exit.setOnAction(event -> {
			 clearTextFieldsInPane(ccpane);
		     clearTextFieldsInPane(despane);
		     clearTextFieldsInPane(aespane);
			loginVBox.getChildren().forEach(node -> {
				
				if (node instanceof TextField) {
					((TextField) node).clear();
				}
				else if (node instanceof Text) {
		            ((Text) node).setText("");    
		        }	
			});
			mainStage.setScene(getLoginScene(loginVBox));
		});
	}

	private void CaesarCipher(VBox ccpane) {
		Text title = new Text();
		title.setText("Caesar Cipher");
		title.setFont(Font.font("Arial", FontWeight.NORMAL, 40));
		title.setFill(Color.WHITE);

		Label inputLabel = new Label("Enter your text below ");
		inputLabel.setTextFill(Color.WHITESMOKE);
		TextField input = new TextField();
		input.setPromptText("Text:");
		input.setPrefSize(40, 20);
		input.setMaxWidth(200);
		input.setMaxHeight(40);

		Label keyLabel = new Label("Provide a key to encrypt your text");
		keyLabel.setTextFill(Color.WHITE);
		TextField key = new TextField();
		key.setPromptText("Key:");
		key.setPrefSize(40, 20);
		key.setMaxWidth(200);
		key.setMaxHeight(40);

		Label keyNameL = new Label("Provide a name for your key");
		keyNameL.setTextFill(Color.WHITE);
		TextField keyName = new TextField();
		keyName.setPromptText("Key Name:");
		keyName.setPrefSize(40, 20);
		keyName.setMaxWidth(200);
		keyName.setMaxHeight(40);

		Button saveKey = new Button();
		saveKey.setPrefSize(100, 20);
		saveKey.setText("Save Key");
		saveKey.setFont(Font.font("Calibri", FontWeight.BOLD, 14));

		Button loadKey = new Button();
		loadKey.setPrefSize(100, 20);
		loadKey.setText("Load Key");
		loadKey.setFont(Font.font("Calibri", FontWeight.BOLD, 14));
		HBox keyGroup = new HBox();
		keyGroup.getChildren().addAll(loadKey, saveKey);
		keyGroup.setMaxWidth(500);
		keyGroup.setAlignment(Pos.CENTER);

		Button encryptBtn = new Button();
		encryptBtn.setPrefSize(100, 20);
		encryptBtn.setText("Encrypt");
		encryptBtn.setFont(Font.font("Calibri", FontWeight.BOLD, 20));

		Button decryptBtn = new Button();
		decryptBtn.setPrefSize(100, 20);
		decryptBtn.setText("Decrypt");
		decryptBtn.setFont(Font.font("Calibri", FontWeight.BOLD, 20));

		HBox buttonGroup = new HBox();
		buttonGroup.getChildren().addAll(encryptBtn, decryptBtn);
		buttonGroup.setMaxWidth(500);
		buttonGroup.setAlignment(Pos.CENTER);

		Label result = new Label("Encrypted text");
		result.setTextFill(Color.WHITE);
		TextField outputResult = new TextField();
		outputResult.setPromptText("Text Result");
		outputResult.setPrefSize(40, 20);
		outputResult.setMaxWidth(200);
		outputResult.setMaxHeight(40);
		
		Label successMessage = new Label();
		successMessage.setTextFill(Color.WHITE);

		ccpane.getChildren().clear();
		ccpane.setAlignment(Pos.CENTER);
		ccpane.setPadding(new Insets(25, 25, 25, 25));
		ccpane.setSpacing(10);

		ccpane.getChildren().addAll(title, inputLabel, input,  keyLabel, key, keyNameL, keyName,buttonGroup, keyGroup,successMessage,
				result, outputResult);

		encryptBtn.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {

				try {
					CaesarCipher cc1 = new CaesarCipher(Integer.parseInt(key.getText()));

					String plainText = input.getText();
					String encryptedText = cc1.encrypt(plainText);
					outputResult.setText(encryptedText);
					successMessage.setText(" ");

				} catch (Exception e) {
					System.out.println("Error in CC: " + e);
					e.printStackTrace();
				}

			}
		});

		decryptBtn.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {

				try {
					CaesarCipher cc1 = new CaesarCipher(Integer.parseInt(key.getText()));

					String encryptedText = input.getText();
					System.out.println(encryptedText + "cc");
					String decryptedText = cc1.decrypt(encryptedText);
					result.setText("Decrypted text");
					outputResult.setText(decryptedText);
					successMessage.setText(" ");
					System.out.println(decryptedText + "cc");

				} catch (Exception e) {
					System.out.println("Error in CC: " + e);
					e.printStackTrace();
				}

			}
		});
		saveKey.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {

				String keyfilePath = "src\\keyFile.txt";
				try {
					CaesarCipher cc3 = new CaesarCipher();
					cc3.saveKeyFile(keyfilePath);
				} catch (Exception e) {
					e.printStackTrace();
				}

				try {
					String keyToSave = key.getText();
					desAlgo.setSecretkey(masterKey);
					byte[] encryptedKey = desAlgo.encrypt(keyToSave);
					String insertQuery = "INSERT INTO userdata.keyinfo(userKey, nameKey, userId) VALUES (?, ?,?)";
					String keyNameSave = keyName.getText();
					
					try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
						preparedStatement.setBytes(1, encryptedKey);
						preparedStatement.setString(2, keyNameSave);
						preparedStatement.setInt(3, loggedInUser);
						preparedStatement.executeUpdate();
						clearTextFields(input, key, keyName, outputResult);
						successMessage.setText("Key saved successfully in the database!");
						System.out.println("Keys saved successfully");
					} catch (SQLException e) {
						System.out.println("Error:" + e);
						e.printStackTrace();
					}

				} catch (Exception e) {
					System.out.println("Error:" + e);
					e.printStackTrace();
				}
			}
		});
		loadKey.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {

				try {
					String keyNameLoad = keyName.getText();
					String selectQuery = "SELECT userKey FROM userdata.keyinfo Where nameKey = ? AND userId = ?";

					try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
						preparedStatement.setString(1, keyNameLoad);
						preparedStatement.setInt(2, loggedInUser);
						
						ResultSet resultSet = preparedStatement.executeQuery();
						if (resultSet.next()) {
							byte [] loadedKey = resultSet.getBytes("userKey");
							String decryptedKey = desAlgo.decrypt(loadedKey);
							key.setText(decryptedKey);
							successMessage.setText("Key loaded successfully!");
							System.out.println("Key loaded successfully: " + decryptedKey);
						} else {
							Alert errAlert = new Alert(AlertType.ERROR);
							errAlert.setContentText("Key not found for the given name");
							errAlert.showAndWait();
							System.out.println("Key not found for the given name");

						}
					} catch (SQLException e) {
						System.out.println("Error:" + e);
						e.printStackTrace();
					}

				} catch (Exception e) {
					System.out.println("Error:" + e);
					e.printStackTrace();
				}
			}
		});
	}

	private void AES(VBox aespane) {
		Text title = new Text();
		title.setText("AES");
		title.setFont(Font.font("Arial", FontWeight.NORMAL, 40));
		title.setFill(Color.WHITE);

		Label inputLabel = new Label("Enter your text below ");
		inputLabel.setTextFill(Color.WHITESMOKE);
		TextField input = new TextField();
		input.setPromptText("Text:");
		input.setPrefSize(40, 20);
		input.setMaxWidth(200);
		input.setMaxHeight(40);

		Label keyLabel = new Label("Key");
		keyLabel.setTextFill(Color.WHITE);
		TextField key = new TextField();
		key.setPromptText("Key:");
		key.setPrefSize(40, 20);
		key.setMaxWidth(200);
		key.setMaxHeight(40);

		Label keyNameL = new Label("Provide a name for your key");
		keyNameL.setTextFill(Color.WHITE);
		TextField keyName = new TextField();
		keyName.setPromptText("Key Name:");
		keyName.setPrefSize(40, 20);
		keyName.setMaxWidth(200);
		keyName.setMaxHeight(40);

		Button saveKey = new Button();
		saveKey.setPrefSize(100, 20);
		saveKey.setText("Save Key");
		saveKey.setFont(Font.font("Calibri", FontWeight.BOLD, 14));

		Button loadKey = new Button();
		loadKey.setPrefSize(100, 20);
		loadKey.setText("Load Key");
		loadKey.setFont(Font.font("Calibri", FontWeight.BOLD, 14));
		HBox keyGroup = new HBox();
		keyGroup.getChildren().addAll(loadKey, saveKey);
		keyGroup.setMaxWidth(500);
		keyGroup.setAlignment(Pos.CENTER);

		Button encryptBtn = new Button();
		encryptBtn.setPrefSize(100, 20);
		encryptBtn.setText("Encrypt");
		encryptBtn.setFont(Font.font("Calibri", FontWeight.BOLD, 20));

		Button decryptBtn = new Button();
		decryptBtn.setPrefSize(100, 20);
		decryptBtn.setText("Decrypt");
		decryptBtn.setFont(Font.font("Calibri", FontWeight.BOLD, 20));

		HBox buttonGroup = new HBox();
		buttonGroup.getChildren().addAll(encryptBtn, decryptBtn);
		buttonGroup.setMaxWidth(500);
		buttonGroup.setAlignment(Pos.CENTER);

		Label result = new Label("Encrypted text");
		result.setTextFill(Color.WHITE);
		TextField outputResult = new TextField();
		outputResult.setPromptText("Textresult");
		outputResult.setPrefSize(40, 20);
		outputResult.setMaxWidth(200);
		outputResult.setMaxHeight(40);
		
		Label successMessage = new Label();
		successMessage.setTextFill(Color.WHITE);

		aespane.getChildren().clear();
		aespane.setAlignment(Pos.CENTER);
		aespane.setPadding(new Insets(25, 25, 25, 25));
		aespane.setSpacing(10);

		aespane.getChildren().addAll(title, inputLabel, input, buttonGroup, keyLabel, key,keyNameL, keyName, keyGroup,successMessage, result,
				outputResult);

		encryptBtn.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {

				try {
					AES aes1 = new AES();
					String msg = input.getText();
					if(!key.getText().isEmpty()) {
						SecretKey keyToEncrypt = new SecretKeySpec(Base64.getDecoder().decode(key.getText()),"DES");
						aes1.setSecretkey(keyToEncrypt);
					}

					System.out.println("The plain text: " + msg);
					byte[] encText = aes1.encrypt(msg);
					outputResult.setText((Base64.getEncoder().encodeToString(encText)));
					System.out.println("The AES encrypted message 64: " + (Base64.getEncoder().encodeToString(encText)));

					String encodedKey = Base64.getEncoder().encodeToString(aes1.getSecretkey().getEncoded());
					System.out.println(encodedKey);
					key.setText(encodedKey);
					successMessage.setText(" ");

				} catch (Exception e) {
					System.out.println("Error in AES: " + e);
					e.printStackTrace();
				}
			}
		});

		decryptBtn.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {

				try {
					AES aes2 = new AES();
					inputLabel.setText("Enter the encrypted text");
					String encText = input.getText();
					keyLabel.setText("Key for decoding");
					byte decodedKey[] = Base64.getDecoder().decode(key.getText());

					SecretKey sp = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
					aes2.setSecretkey(sp);
					String decText = aes2.decrypt(Base64.getDecoder().decode(encText));
					result.setText("Decrypted text");
					outputResult.setText(decText);
					successMessage.setText(" ");
					System.out.println(decText);

				} catch (Exception e) {
					System.out.println("Error in AES: " + e);
					e.printStackTrace();
				}
			}
		});
		saveKey.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {

				String keyfilePath = "src\\keyFile.txt";
				try {
					AES aes3 = new AES();
					aes3.saveKeyFile(keyfilePath);
				} catch (Exception e) {
					e.printStackTrace();
				}

				try {
					
					String keyToSave = key.getText();
					desAlgo.setSecretkey(masterKey);
					byte[] encryptedKey = desAlgo.encrypt(keyToSave);
					String insertQuery = "INSERT INTO userdata.keyinfo(userKey, nameKey, userId) VALUES (?, ?,?)";
					String keyNameSave = keyName.getText(); 

					try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
						preparedStatement.setBytes(1, encryptedKey);
						preparedStatement.setString(2, keyNameSave);
						preparedStatement.setInt(3, loggedInUser);
						preparedStatement.executeUpdate();
						clearTextFields(input, key, keyName,outputResult);
						successMessage.setText("Key saved successfully in the database!");
						System.out.println("Keys saved successfully");
					} catch (SQLException e) {
						System.out.println("Error:" + e);
						e.printStackTrace();
						successMessage.setText("Error saving key. ");
					}

				} catch (Exception e) {
					System.out.println("Error:" + e);
					e.printStackTrace();
				}
			}
		});

		loadKey.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {

				try {
			
					String keyNameLoad = keyName.getText();
					String selectQuery = "SELECT userKey FROM userdata.keyinfo Where nameKey = ? AND userId = ?";

					try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
						preparedStatement.setString(1, keyNameLoad);
						preparedStatement.setInt(2, loggedInUser);
						
						ResultSet resultSet = preparedStatement.executeQuery();
						if (resultSet.next()) {
							byte [] loadedKey = resultSet.getBytes("userKey");
							String decryptedKey = desAlgo.decrypt(loadedKey);
							key.setText(decryptedKey);
							successMessage.setText("Key loaded successfully!");
							System.out.println("Key loaded successfully: " + decryptedKey);
						} else {
							Alert errAlert = new Alert(AlertType.ERROR);
							errAlert.setContentText("Key not found for the given name");
							errAlert.showAndWait();
							System.out.println("Key not found for the given name");


						}
					} catch (SQLException e) {
						System.out.println("Error:" + e);
						e.printStackTrace();
					}

				} catch (Exception e) {
					System.out.println("Error:" + e);
					e.printStackTrace();
				}
			}
		});

	}

	private void DES(VBox despane) {
		Text title = new Text();
		title.setText("DES");
		title.setFont(Font.font("Arial", FontWeight.NORMAL, 40));
		title.setFill(Color.WHITE);

		Label inputLabel = new Label("Enter your text below ");
		inputLabel.setTextFill(Color.WHITESMOKE);
		TextField input = new TextField();
		input.setPromptText("Text:");
		input.setPrefSize(40, 20);
		input.setMaxWidth(200);
		input.setMaxHeight(40);

		Label keyLabel = new Label("Key");
		keyLabel.setTextFill(Color.WHITE);
		TextField key = new TextField();
		key.setPromptText("Key:");
		key.setPrefSize(40, 20);
		key.setMaxWidth(200);
		key.setMaxHeight(40);

		Label keyNameL = new Label("Provide a name for your key");
		keyNameL.setTextFill(Color.WHITE);
		TextField keyName = new TextField();
		keyName.setPromptText("Key Name:");
		keyName.setPrefSize(40, 20);
		keyName.setMaxWidth(200);
		keyName.setMaxHeight(40);

		Button saveKey = new Button();
		saveKey.setPrefSize(100, 20);
		saveKey.setText("Save Key");
		saveKey.setFont(Font.font("Calibri", FontWeight.BOLD, 14));

		Button loadKey = new Button();
		loadKey.setPrefSize(100, 20);
		loadKey.setText("Load Key");
		loadKey.setFont(Font.font("Calibri", FontWeight.BOLD, 14));
		HBox keyGroup = new HBox();
		keyGroup.getChildren().addAll(loadKey, saveKey);
		keyGroup.setMaxWidth(500);
		keyGroup.setAlignment(Pos.CENTER);

		Button encryptBtn = new Button();
		encryptBtn.setPrefSize(100, 20);
		encryptBtn.setText("Encrypt");
		encryptBtn.setFont(Font.font("Calibri", FontWeight.BOLD, 20));

		Button decryptBtn = new Button();
		decryptBtn.setPrefSize(100, 20);
		decryptBtn.setText("Decrypt");
		decryptBtn.setFont(Font.font("Calibri", FontWeight.BOLD, 20));

		HBox buttonGroup = new HBox();
		buttonGroup.getChildren().addAll(encryptBtn, decryptBtn);
		buttonGroup.setMaxWidth(500);
		buttonGroup.setAlignment(Pos.CENTER);

		Label result = new Label("Encrypted text");
		result.setTextFill(Color.WHITE);
		TextField outputResult = new TextField();
		outputResult.setPromptText("Text Result");
		outputResult.setPrefSize(40, 20);
		outputResult.setMaxWidth(200);
		outputResult.setMaxHeight(40);
		
		Label successMessage = new Label();
		successMessage.setTextFill(Color.WHITE);

		despane.getChildren().clear();
		despane.setAlignment(Pos.CENTER);
		despane.setPadding(new Insets(25, 25, 25, 25));
		despane.setSpacing(10);

		despane.getChildren().addAll(title, inputLabel, input, buttonGroup, keyLabel, key, keyNameL, keyName, keyGroup,successMessage,
				result, outputResult);

		encryptBtn.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {

				try {
					DES des1 = new DES();
					String msg = input.getText();
					
					if(!key.getText().isEmpty()) {
						SecretKey keyToEncrypt = new SecretKeySpec(Base64.getDecoder().decode(key.getText()),"DES");
						des1.setSecretkey(keyToEncrypt);
					}

					System.out.println("The plain text: " + msg);
					byte[] encText = des1.encrypt(msg);
					outputResult.setText((Base64.getEncoder().encodeToString(encText)));
					System.out.println("The DES encrypted message 64: " + (Base64.getEncoder().encodeToString(encText)));

					String encodedKey = Base64.getEncoder().encodeToString(des1.getSecretkey().getEncoded());
					System.out.println(encodedKey);
					key.setText(encodedKey);
					successMessage.setText(" ");

				} catch (Exception e) {
					System.out.println("Error in DES: " + e);
					e.printStackTrace();
				}
			}
		});

		decryptBtn.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {

				try {
					DES des2 = new DES();
					inputLabel.setText("Enter the encrypted text");
					String encText = input.getText();
					keyLabel.setText("Key for decoding");
					byte decodedKey[] = Base64.getDecoder().decode(key.getText());

					SecretKey sp = new SecretKeySpec(decodedKey, 0, decodedKey.length, "DES");
					des2.setSecretkey(sp);
					String decText = des2.decrypt(Base64.getDecoder().decode(encText));
					result.setText("Decrypted text");
					outputResult.setText(decText);
					successMessage.setText(" ");
					System.out.println(decText);

				} catch (Exception e) {
					System.out.println("Error in DES: " + e);
					e.printStackTrace();
				}
			}
		});

		saveKey.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {

				String keyfilePath = "src\\keyFile.txt";
				try {
					DES des3 = new DES();
					des3.saveKeyFile(keyfilePath);
				} catch (Exception e) {
					e.printStackTrace();
				}

				try {

					String keyToSave = key.getText();
					desAlgo.setSecretkey(masterKey);
					byte[] encryptedKey = desAlgo.encrypt(keyToSave);
					String insertQuery = "INSERT INTO encrypt_decrypt.keyinfo(userKey, nameKey, userId) VALUES (?, ?,?)";
					String keyNameSave = keyName.getText();
					
					try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
						preparedStatement.setBytes(1, encryptedKey);
						preparedStatement.setString(2, keyNameSave);
						preparedStatement.setInt(3, loggedInUser);
						preparedStatement.executeUpdate();
						clearTextFields(input, key, keyName, outputResult);
						successMessage.setText("Key saved successfully in the database!");
						System.out.println("Keys saved successfully");
					} catch (SQLException e) {
						System.out.println("Error:" + e);
						e.printStackTrace();
						successMessage.setText("Error saving key. ");
					}

				} catch (Exception e) {
					System.out.println("Error:" + e);
					e.printStackTrace();
				}
			}
		});

		loadKey.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {

				try {
					String keyNameLoad = keyName.getText();
					String selectQuery = "SELECT userKey FROM userdata.keyinfo Where nameKey = ? AND userId = ?";

					try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
						preparedStatement.setString(1, keyNameLoad);
						preparedStatement.setInt(2, loggedInUser);
						
						ResultSet resultSet = preparedStatement.executeQuery();
						if (resultSet.next()) {
							byte [] loadedKey = resultSet.getBytes("userKey");
							String decryptedKey = desAlgo.decrypt(loadedKey);
							key.setText(decryptedKey);
							successMessage.setText("Key loaded successfully!");
							System.out.println("Key loaded successfully: " + decryptedKey);
						} else {
							Alert errAlert = new Alert(AlertType.ERROR);
							errAlert.setContentText("Key not found for the given name");
							errAlert.showAndWait();
							System.out.println("Key not found for the given name");
						}
					} catch (SQLException e) {
						System.out.println("Error:" + e);
						e.printStackTrace();
					}

				} catch (Exception e) {
					System.out.println("Error:" + e);
					e.printStackTrace();
				}
			}
		});
	}

	private Scene getLoginScene(VBox elements) {
		BorderPane root = new BorderPane();
		Image backgrndImage = new Image("/loginimg.jpg");
		ImageView imageView = new ImageView(backgrndImage);
		imageView.setImage(backgrndImage);
		imageView.setFitWidth(1000);
		imageView.setFitHeight(950);
		root.getChildren().add(imageView);
		root.setCenter(elements);
		Scene scene = new Scene(root, 1000, 750);
		return scene;
	}
	
	private Connection connectToDB() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
			return connection;
		} catch (ClassNotFoundException | SQLException e) {
			Alert a = new Alert(AlertType.ERROR);
			a.setContentText("Error connecting to DB");
			a.showAndWait();
			e.printStackTrace();
			return null;
		} 		
	}
	private void clearTextFields(TextField... textFields) {
	    for (TextField textField : textFields) {
	        textField.clear();
	    }
	}
	public void clearTextFieldsInPane(VBox pane) {
	    pane.getChildren().forEach(node -> {
	        if (node instanceof TextField) {
	            ((TextField) node).clear();
	        } 
	    });
	}
	public static void main(String[] args) {
		launch(args);
	}
}

import java.awt.*;
import java.awt.event.*;
import javax.swing.JOptionPane;
import java.io.*;
import java.util.Date;
import java.util.HashMap;
import java.text.SimpleDateFormat;

class TicTacToeGame extends Frame implements ActionListener, ComponentListener
{
	private Button[] gameButtons = new Button[9];
	private Button statusButton = new Button("Game Status");
	private Button hintButton = new Button("HINT");
	private Button leaveButton = new Button("LEAVE");
	private Label titleLabel = new Label("TIC-TAC-TOE CHAMPIONSHIP", Label.CENTER);
	private int moveCount = 0;
	private boolean gameOver = false;
	private String currentPlayer = "X";
	private boolean isResizing = false;
	private int lastWidth = 0;
	private int lastHeight = 0;
	
	// Player setup components
	private boolean gameStarted = false;
	private String player1Name = "Player 1";
	private String player2Name = "Player 2";
	private Label welcomeLabel;
	private Label player1Label;
	private Label player2Label;
	private TextField player1Field;
	private TextField player2Field;
	private Button startButton;
	
	// Competition mode components
	private Checkbox competitionCheckbox;
	private Label roundsLabel;
	private TextField roundsField;
	
	// Game moves tracking
	private java.util.List<String> gameRecord = new java.util.ArrayList<>();
	private int currentMoveNumber = 1;
	
	// Competition mode variables
	private boolean competitionMode = false;
	private int totalRounds = 1;
	private int currentRound = 1;
	private int player1Wins = 0;
	private int player2Wins = 0;
	private int draws = 0;
	private java.util.List<java.util.List<String>> allRoundsRecord = new java.util.ArrayList<>();
	private java.util.List<String> roundResults = new java.util.ArrayList<>();
	
	// New features
	private boolean soundEnabled = true;
	private String selectedTheme = "Classic";
	private int gameSpeed = 1; // 1=Normal, 2=Fast, 3=Slow
	private boolean showAnimations = true;
	private HashMap<String, Integer> playerStats = new HashMap<>();
	private long gameStartTime;
	private long totalGameTime = 0;
	private Button soundToggleButton;
	private Button themeButton;
	private Button statsButton;
	private Button saveGameButton;
	private Choice difficultyChoice;
	private boolean vsComputer = false;
	private String aiDifficulty = "Medium";
	
	// Win condition patterns (row, column, diagonal indices)
	private final int[][] WIN_PATTERNS = {
		{0, 1, 2}, {3, 4, 5}, {6, 7, 8}, // rows
		{0, 3, 6}, {1, 4, 7}, {2, 5, 8}, // columns
		{0, 4, 8}, {2, 4, 6}              // diagonals
	};
	
	public TicTacToeGame()
	{
		initializePlayerStats();
		initializeFrame();
		showPlayerSetupPanel();
		
		// Initialize resize tracking
		lastWidth = getWidth();
		lastHeight = getHeight();
		
		// Add component listener for window resize events
		addComponentListener(this);
	}
	
	private void initializeFrame()
	{
		setTitle("Tic Tac Toe Game");
		setLayout(null);
		Font gameFont = new Font("Arial", Font.BOLD, 30);
		setFont(gameFont);
		// Remove setResizable(false) to allow window resizing
		
		// Set theme-based background color
		applyTheme();
	}
	
	private void initializePlayerStats()
	{
		playerStats.put("totalGames", 0);
		playerStats.put("wins", 0);
		playerStats.put("losses", 0);
		playerStats.put("draws", 0);
		playerStats.put("winStreak", 0);
		playerStats.put("bestWinStreak", 0);
	}
	
	private void applyTheme()
	{
		switch(selectedTheme) {
			case "Dark":
				setBackground(new Color(45, 45, 45));
				break;
			case "Ocean":
				setBackground(new Color(135, 206, 250));
				break;
			case "Sunset":
				setBackground(new Color(255, 218, 185));
				break;
			case "Forest":
				setBackground(new Color(144, 238, 144));
				break;
			default: // Classic
				setBackground(new Color(240, 248, 255));
				break;
		}
	}
	
	private void showPlayerSetupPanel()
	{
		int windowWidth = getWidth();
		int windowHeight = getHeight();
		
		// Welcome message
		welcomeLabel = new Label("Welcome to Tic-Tac-Toe Championship!", Label.CENTER);
		welcomeLabel.setSize(windowWidth - 40, 40);
		welcomeLabel.setLocation(20, 80);
		welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
		welcomeLabel.setForeground(new Color(25, 25, 112)); // Midnight Blue
		add(welcomeLabel);
		
		// Player 1 setup - fixed positioning responsive to window width
		int labelStartX = Math.max(50, (windowWidth - 400) / 2);
		int fieldStartX = labelStartX + 150;
		
		player1Label = new Label("Player 1 Name (X):");
		player1Label.setSize(150, 30);
		player1Label.setLocation(labelStartX, 180);
		player1Label.setFont(new Font("Arial", Font.BOLD, 14));
		player1Label.setForeground(new Color(220, 20, 60)); // Crimson for X
		add(player1Label);
		
		player1Field = new TextField();
		player1Field.setSize(200, 30);
		player1Field.setLocation(fieldStartX, 180);
		player1Field.setFont(new Font("Arial", Font.PLAIN, 14));
		// Add focus listener for placeholder effect and validation
		player1Field.addFocusListener(new FocusAdapter() {
			public void focusGained(FocusEvent e) {
				if(player1Field.getText().equals("Enter Player 1 name...")) {
					player1Field.setText("");
					player1Field.setForeground(Color.BLACK);
				}
			}
			public void focusLost(FocusEvent e) {
				String text = player1Field.getText().trim();
				if(text.isEmpty()) {
					player1Field.setText("Enter Player 1 name...");
					player1Field.setForeground(Color.GRAY);
				} else if(!isValidName(text)) {
					player1Field.setForeground(Color.RED);
				} else {
					player1Field.setForeground(Color.BLACK);
				}
			}
		});
		player1Field.setText("Enter Player 1 name...");
		player1Field.setForeground(Color.GRAY);
		add(player1Field);
		
		// Player 2 setup - same responsive positioning
		player2Label = new Label("Player 2 Name (O):");
		player2Label.setSize(150, 30);
		player2Label.setLocation(labelStartX, 230);
		player2Label.setFont(new Font("Arial", Font.BOLD, 14));
		player2Label.setForeground(new Color(0, 128, 0)); // Green for O
		add(player2Label);
		
		player2Field = new TextField();
		player2Field.setSize(200, 30);
		player2Field.setLocation(fieldStartX, 230);
		player2Field.setFont(new Font("Arial", Font.PLAIN, 14));
		// Add focus listener for placeholder effect and validation
		player2Field.addFocusListener(new FocusAdapter() {
			public void focusGained(FocusEvent e) {
				if(player2Field.getText().equals("Enter Player 2 name...")) {
					player2Field.setText("");
					player2Field.setForeground(Color.BLACK);
				}
			}
			public void focusLost(FocusEvent e) {
				String text = player2Field.getText().trim();
				if(text.isEmpty()) {
					player2Field.setText("Enter Player 2 name...");
					player2Field.setForeground(Color.GRAY);
				} else if(!isValidName(text)) {
					player2Field.setForeground(Color.RED);
				} else {
					player2Field.setForeground(Color.BLACK);
				}
			}
		});
		player2Field.setText("Enter Player 2 name...");
		player2Field.setForeground(Color.GRAY);
		add(player2Field);
		
		// Competition mode checkbox
		competitionCheckbox = new Checkbox("Competition Mode");
		competitionCheckbox.setSize(200, 30);
		competitionCheckbox.setLocation(labelStartX, 280);
		competitionCheckbox.setFont(new Font("Arial", Font.BOLD, 14));
		competitionCheckbox.setForeground(new Color(128, 0, 128)); // Purple
		competitionCheckbox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if(competitionCheckbox.getState()) {
					// Show rounds input
					roundsLabel.setVisible(true);
					roundsField.setVisible(true);
				} else {
					// Hide rounds input
					roundsLabel.setVisible(false);
					roundsField.setVisible(false);
				}
			}
		});
		add(competitionCheckbox);
		
		// VS Computer checkbox
		Checkbox vsComputerCheckbox = new Checkbox("VS Computer");
		vsComputerCheckbox.setSize(150, 30);
		vsComputerCheckbox.setLocation(labelStartX + 220, 280);
		vsComputerCheckbox.setFont(new Font("Arial", Font.BOLD, 14));
		vsComputerCheckbox.setForeground(new Color(255, 69, 0)); // Red Orange
		vsComputerCheckbox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				vsComputer = vsComputerCheckbox.getState();
				if(vsComputer) {
					player2Field.setText("Computer");
					player2Field.setEnabled(false);
					difficultyChoice.setVisible(true);
				} else {
					player2Field.setText("Enter Player 2 name...");
					player2Field.setForeground(Color.GRAY);
					player2Field.setEnabled(true);
					difficultyChoice.setVisible(false);
				}
			}
		});
		add(vsComputerCheckbox);
		
		// AI Difficulty selection (initially hidden)
		difficultyChoice = new Choice();
		difficultyChoice.add("Easy");
		difficultyChoice.add("Medium");
		difficultyChoice.add("Hard");
		difficultyChoice.add("Expert");
		difficultyChoice.select("Medium");
		difficultyChoice.setSize(100, 30);
		difficultyChoice.setLocation(labelStartX + 380, 280);
		difficultyChoice.setFont(new Font("Arial", Font.PLAIN, 12));
		difficultyChoice.setVisible(false);
		difficultyChoice.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				aiDifficulty = difficultyChoice.getSelectedItem();
			}
		});
		add(difficultyChoice);
		
		// Rounds input (initially hidden)
		roundsLabel = new Label("Number of Rounds:");
		roundsLabel.setSize(150, 30);
		roundsLabel.setLocation(labelStartX, 320);
		roundsLabel.setFont(new Font("Arial", Font.BOLD, 14));
		roundsLabel.setForeground(new Color(128, 0, 128)); // Purple
		roundsLabel.setVisible(false);
		add(roundsLabel);
		
		roundsField = new TextField();
		roundsField.setSize(100, 30);
		roundsField.setLocation(fieldStartX, 320);
		roundsField.setFont(new Font("Arial", Font.PLAIN, 14));
		roundsField.setVisible(false);
		// Add focus listener for placeholder effect
		roundsField.addFocusListener(new FocusAdapter() {
			public void focusGained(FocusEvent e) {
				if(roundsField.getText().equals("Enter rounds...")) {
					roundsField.setText("");
					roundsField.setForeground(Color.BLACK);
				}
			}
			public void focusLost(FocusEvent e) {
				String text = roundsField.getText().trim();
				if(text.isEmpty()) {
					roundsField.setText("Enter rounds...");
					roundsField.setForeground(Color.GRAY);
				} else {
					roundsField.setForeground(Color.BLACK);
				}
			}
		});
		roundsField.setText("Enter rounds...");
		roundsField.setForeground(Color.GRAY);
		add(roundsField);
		
		// Start button - properly centered and responsive
		int buttonWidth = 180;
		int buttonHeight = 60;
		startButton = new Button("START GAME");
		startButton.setSize(buttonWidth, buttonHeight);
		startButton.setLocation((windowWidth - buttonWidth) / 2, 370);
		startButton.setBackground(new Color(34, 139, 34)); // Forest Green
		startButton.setForeground(Color.WHITE);
		startButton.setFont(new Font("Arial", Font.BOLD, 18));
		startButton.addActionListener(this);
		add(startButton);
		
		// Additional control buttons
		int controlY = 440;
		int buttonSpacing = 110;
		int startX = (windowWidth - (buttonSpacing * 4)) / 2;
		
		// Sound toggle button
		soundToggleButton = new Button(soundEnabled ? "SOUND ON" : "SOUND OFF");
		soundToggleButton.setSize(100, 40);
		soundToggleButton.setLocation(startX, controlY);
		soundToggleButton.setBackground(new Color(70, 130, 180));
		soundToggleButton.setForeground(Color.WHITE);
		soundToggleButton.setFont(new Font("Arial", Font.BOLD, 12));
		soundToggleButton.addActionListener(this);
		add(soundToggleButton);
		
		// Theme button
		themeButton = new Button("THEME");
		themeButton.setSize(100, 40);
		themeButton.setLocation(startX + buttonSpacing, controlY);
		themeButton.setBackground(new Color(138, 43, 226));
		themeButton.setForeground(Color.WHITE);
		themeButton.setFont(new Font("Arial", Font.BOLD, 12));
		themeButton.addActionListener(this);
		add(themeButton);
		
		// Stats button
		statsButton = new Button("STATS");
		statsButton.setSize(100, 40);
		statsButton.setLocation(startX + buttonSpacing * 2, controlY);
		statsButton.setBackground(new Color(255, 140, 0));
		statsButton.setForeground(Color.WHITE);
		statsButton.setFont(new Font("Arial", Font.BOLD, 12));
		statsButton.addActionListener(this);
		add(statsButton);
		
		// Save Game button
		saveGameButton = new Button("SAVE GAME");
		saveGameButton.setSize(100, 40);
		saveGameButton.setLocation(startX + buttonSpacing * 3, controlY);
		saveGameButton.setBackground(new Color(50, 205, 50));
		saveGameButton.setForeground(Color.WHITE);
		saveGameButton.setFont(new Font("Arial", Font.BOLD, 12));
		saveGameButton.addActionListener(this);
		add(saveGameButton);
		
		repaint();
	}
	
	// Validation method to check if name contains at least one alphabet character
	private boolean isValidName(String name)
	{
		if(name == null || name.trim().isEmpty()) {
			return false;
		}
		
		// Check if the name contains at least one alphabet character
		for(char c : name.toCharArray()) {
			if(Character.isLetter(c)) {
				return true;
			}
		}
		return false;
	}
	
	private void startGame()
	{
		// Get player names, handling placeholder text
		String field1Text = player1Field.getText().trim();
		String field2Text = player2Field.getText().trim();
		
		// Validate player names
		boolean player1Valid = true;
		boolean player2Valid = true;
		
		// Check Player 1 name
		if(field1Text.equals("Enter Player 1 name...") || field1Text.isEmpty()) {
			player1Name = "Player 1";
		} else if(!isValidName(field1Text)) {
			player1Valid = false;
			player1Field.setForeground(Color.RED);
		} else {
			player1Name = field1Text;
			player1Field.setForeground(Color.BLACK);
		}
		
		// Check Player 2 name
		if(vsComputer) {
			player2Name = "Computer (" + aiDifficulty + ")";
		} else if(field2Text.equals("Enter Player 2 name...") || field2Text.isEmpty()) {
			player2Name = "Player 2";
		} else if(!isValidName(field2Text)) {
			player2Valid = false;
			player2Field.setForeground(Color.RED);
		} else {
			player2Name = field2Text;
			player2Field.setForeground(Color.BLACK);
		}
		
		// If validation fails, show error message and don't start the game
		if(!player1Valid || (!player2Valid && !vsComputer)) {
			String errorMessage = "Invalid name(s) detected!\n\n";
			if(!player1Valid && !player2Valid) {
				errorMessage += "Both player names must contain at least one alphabet character.\n";
			} else if(!player1Valid) {
				errorMessage += "Player 1 name must contain at least one alphabet character.\n";
			} else {
				errorMessage += "Player 2 name must contain at least one alphabet character.\n";
			}
			errorMessage += "Names cannot contain only numbers or special characters.";
			
			JOptionPane.showMessageDialog(
				this,
				errorMessage,
				"Invalid Player Names",
				JOptionPane.ERROR_MESSAGE
			);
			return; // Don't start the game
		}
		
		// Check competition mode
		competitionMode = competitionCheckbox.getState();
		if(competitionMode) {
			try {
				String roundsText = roundsField.getText().trim();
				// Handle placeholder text
				if(roundsText.equals("Enter rounds...") || roundsText.isEmpty()) {
					JOptionPane.showMessageDialog(
						this,
						"Please enter the number of rounds for competition mode!",
						"Missing Rounds",
						JOptionPane.ERROR_MESSAGE
					);
					return;
				}
				
				totalRounds = Integer.parseInt(roundsText);
				if(totalRounds < 1 || totalRounds > 10) {
					JOptionPane.showMessageDialog(
						this,
						"Number of rounds must be between 1 and 10!",
						"Invalid Rounds",
						JOptionPane.ERROR_MESSAGE
					);
					return;
				}
			} catch(NumberFormatException ex) {
				JOptionPane.showMessageDialog(
					this,
					"Please enter a valid number for rounds!",
					"Invalid Input",
					JOptionPane.ERROR_MESSAGE
				);
				return;
			}
			
			// Reset competition stats
			currentRound = 1;
			player1Wins = 0;
			player2Wins = 0;
			draws = 0;
			allRoundsRecord.clear();
			roundResults.clear();
		} else {
			totalRounds = 1;
			currentRound = 1;
		}
		
		// Remove setup components
		removeAll();
		
		// Set game started flag
		gameStarted = true;
		gameStartTime = System.currentTimeMillis();
		
		// Create game components
		createTitleLabel();
		createGameBoard();
		createControlButtons();
		updateStatus(getCurrentPlayerName() + "'s turn");
		
		// Apply current theme
		applyTheme();
		
		repaint();
	}
	
	private String getCurrentPlayerName()
	{
		return currentPlayer.equals("X") ? player1Name : player2Name;
	}
	
	private void createTitleLabel()
	{
		int windowWidth = getWidth();
		String titleText = "TIC-TAC-TOE CHAMPIONSHIP";
		if(competitionMode) {
			titleText += " - Round " + currentRound + " of " + totalRounds;
		}
		titleLabel.setText(titleText);
		titleLabel.setSize(windowWidth - 40, 30);
		titleLabel.setLocation(20, 15);
		titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
		titleLabel.setForeground(new Color(25, 25, 112)); // Midnight Blue
		add(titleLabel);
	}
	
	private void createGameBoard()
	{
		int windowWidth = getWidth();
		int windowHeight = getHeight();
		
		// Calculate dynamic button size and positioning
		int buttonSize = Math.min((windowWidth - 120) / 3, (windowHeight - 200) / 3);
		buttonSize = Math.max(buttonSize, 80); // Minimum button size
		
		int totalBoardWidth = buttonSize * 3;
		int totalBoardHeight = buttonSize * 3;
		int startX = (windowWidth - totalBoardWidth) / 2;
		int startY = (windowHeight - totalBoardHeight) / 2;
		
		int buttonIndex = 0;
		
		for(int row = 0; row < 3; row++)
		{
			for(int col = 0; col < 3; col++)
			{
				gameButtons[buttonIndex] = new Button("");
				gameButtons[buttonIndex].setSize(buttonSize, buttonSize);
				gameButtons[buttonIndex].setLocation(startX + (col * buttonSize), startY + (row * buttonSize));
				
				// Dynamic font size based on button size
				int fontSize = Math.max(buttonSize / 3, 20);
				gameButtons[buttonIndex].setFont(new Font("Arial", Font.BOLD, fontSize));
				
				// Beautiful button styling
				gameButtons[buttonIndex].setBackground(new Color(255, 255, 255)); // White background
				gameButtons[buttonIndex].setForeground(new Color(70, 130, 180)); // Steel Blue text
				
				gameButtons[buttonIndex].addActionListener(this);
				add(gameButtons[buttonIndex]);
				buttonIndex++;
			}
		}
	}
	
	private void createControlButtons()
	{
		int windowWidth = getWidth();
		int windowHeight = getHeight();
		
		// Status button with beautiful styling - responsive width
		int statusWidth = Math.min(windowWidth - 40, 400);
		statusButton.setSize(statusWidth, 40);
		statusButton.setLocation((windowWidth - statusWidth) / 2, 50);
		statusButton.setEnabled(false);
		statusButton.setBackground(new Color(70, 130, 180)); // Steel Blue
		statusButton.setForeground(Color.WHITE);
		statusButton.setFont(new Font("Arial", Font.BOLD, 14));
		add(statusButton);
		
		// Calculate positions for hint and leave buttons
		int buttonWidth = 120;
		int buttonHeight = 45;
		int spacing = 30;
		int totalButtonWidth = (buttonWidth * 2) + spacing;
		int startX = (windowWidth - totalButtonWidth) / 2;
		int buttonY = windowHeight - 90;
		
		// Hint button with attractive styling
		hintButton.setSize(buttonWidth, buttonHeight);
		hintButton.setLocation(startX, buttonY);
		hintButton.setBackground(new Color(255, 165, 0)); // Orange
		hintButton.setForeground(Color.WHITE);
		hintButton.setFont(new Font("Arial", Font.BOLD, 14));
		// Remove any existing listeners before adding new one
		ActionListener[] hintListeners = hintButton.getActionListeners();
		for(ActionListener listener : hintListeners) {
			hintButton.removeActionListener(listener);
		}
		hintButton.addActionListener(this);
		add(hintButton);
		
		// Leave button with attractive styling
		leaveButton.setSize(buttonWidth, buttonHeight);
		leaveButton.setLocation(startX + buttonWidth + spacing, buttonY);
		leaveButton.setBackground(new Color(220, 20, 60)); // Crimson
		leaveButton.setForeground(Color.WHITE);
		leaveButton.setFont(new Font("Arial", Font.BOLD, 14));
		// Remove any existing listeners before adding new one
		ActionListener[] leaveListeners = leaveButton.getActionListeners();
		for(ActionListener listener : leaveListeners) {
			leaveButton.removeActionListener(listener);
		}
		leaveButton.addActionListener(this);
		add(leaveButton);
	}
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() == startButton)
		{
			startGame();
			return;
		}
		
		if(e.getSource() == hintButton)
		{
			showHint();
			return;
		}
		
		if(e.getSource() == leaveButton)
		{
			leaveGame();
			return;
		}
		
		if(e.getSource() == soundToggleButton)
		{
			toggleSound();
			return;
		}
		
		if(e.getSource() == themeButton)
		{
			changeTheme();
			return;
		}
		
		if(e.getSource() == statsButton)
		{
			showPlayerStats();
			return;
		}
		
		if(e.getSource() == saveGameButton)
		{
			saveGameToFile();
			return;
		}
		
		// Handle game button clicks
		Button clickedButton = (Button)e.getSource();
		
		// Check if game is over or button already clicked
		if(gameOver || !clickedButton.getLabel().equals(""))
		{
			return;
		}
		
		// Make the move
		makeMove(clickedButton);
		
		// If it's computer's turn and VS Computer is enabled
		if(!gameOver && vsComputer && currentPlayer.equals("O")) {
			// Add delay for better user experience
			try {
				Thread.sleep(500 + (gameSpeed == 3 ? 1000 : gameSpeed == 2 ? 0 : 300));
			} catch(InterruptedException ex) {}
			
			makeComputerMove();
		}
	}
	
	private boolean checkWin(String player)
	{
		for(int[] pattern : WIN_PATTERNS)
		{
			if(gameButtons[pattern[0]].getLabel().equals(player) &&
			   gameButtons[pattern[1]].getLabel().equals(player) &&
			   gameButtons[pattern[2]].getLabel().equals(player))
			{
				// Highlight winning combination
				highlightWinningButtons(pattern);
				return true;
			}
		}
		return false;
	}
	
	private void highlightWinningButtons(int[] winningPattern)
	{
		for(int index : winningPattern)
		{
			gameButtons[index].setBackground(new Color(255, 215, 0)); // Gold background
			gameButtons[index].setForeground(new Color(139, 69, 19)); // Saddle Brown text
		}
	}
	
	private void updateStatus(String message)
	{
		statusButton.setLabel(message);
	}
	
	private void disableAllButtons()
	{
		for(Button button : gameButtons)
		{
			button.setEnabled(false);
		}
	}
	
	private void makeMove(Button clickedButton)
	{
		clickedButton.setLabel(currentPlayer);
		clickedButton.setEnabled(false);
		
		// Play sound effect
		if(soundEnabled) {
			playMoveSound();
		}
		
		// Show animation if enabled
		if(showAnimations) {
			animateButton(clickedButton);
		}
		
		// Record the move
		int buttonIndex = -1;
		for(int i = 0; i < gameButtons.length; i++) {
			if(gameButtons[i] == clickedButton) {
				buttonIndex = i;
				break;
			}
		}
		
		if(buttonIndex != -1) {
			int row = buttonIndex / 3 + 1;
			int col = buttonIndex % 3 + 1;
			String moveRecord = "Move " + currentMoveNumber + ": " + getCurrentPlayerName() + 
			                   " (" + currentPlayer + ") at Row " + row + ", Column " + col;
			gameRecord.add(moveRecord);
			currentMoveNumber++;
		}
		
		// Add beautiful colors for X and O based on theme
		Color xColor, oColor;
		switch(selectedTheme) {
			case "Dark":
				xColor = new Color(255, 99, 71); // Tomato
				oColor = new Color(144, 238, 144); // Light Green
				break;
			case "Ocean":
				xColor = new Color(255, 20, 147); // Deep Pink
				oColor = new Color(0, 191, 255); // Deep Sky Blue
				break;
			case "Sunset":
				xColor = new Color(255, 69, 0); // Red Orange
				oColor = new Color(255, 215, 0); // Gold
				break;
			case "Forest":
				xColor = new Color(139, 69, 19); // Saddle Brown
				oColor = new Color(34, 139, 34); // Forest Green
				break;
			default: // Classic
				xColor = new Color(220, 20, 60); // Crimson
				oColor = new Color(0, 128, 0); // Green
				break;
		}
		
		if(currentPlayer.equals("X")) {
			clickedButton.setForeground(xColor);
		} else {
			clickedButton.setForeground(oColor);
		}
		
		moveCount++;
		
		// Check for win
		if(checkWin(currentPlayer))
		{
			String winnerName = getCurrentPlayerName();
			updateStatus(winnerName + " wins!");
			gameOver = true;
			disableAllButtons();
			
			// Update stats
			updatePlayerStats(winnerName, false);
			
			// Play win sound
			if(soundEnabled) {
				playWinSound();
			}
			
			// Handle round completion
			handleRoundEnd(winnerName, false);
		}
		else if(moveCount == 9)
		{
			updateStatus("It's a draw!");
			gameOver = true;
			
			// Update stats for draw
			updatePlayerStats("", true);
			
			// Play draw sound
			if(soundEnabled) {
				playDrawSound();
			}
			
			// Handle round completion
			handleRoundEnd("", true);
		}
		else
		{
			// Switch player
			currentPlayer = currentPlayer.equals("X") ? "O" : "X";
			updateStatus(getCurrentPlayerName() + "'s turn");
		}
	}
	
	private void makeComputerMove()
	{
		if(gameOver) return;
		
		int move = getComputerMove();
		if(move != -1) {
			makeMove(gameButtons[move]);
		}
	}
	
	private int getComputerMove()
	{
		String[] board = new String[9];
		for(int i = 0; i < 9; i++) {
			board[i] = gameButtons[i].getLabel();
		}
		
		switch(aiDifficulty) {
			case "Easy":
				return getRandomMove(board);
			case "Medium":
				return Math.random() < 0.7 ? getBestMove() : getRandomMove(board);
			case "Hard":
				return Math.random() < 0.9 ? getBestMove() : getRandomMove(board);
			case "Expert":
				return getBestMove();
			default:
				return getBestMove();
		}
	}
	
	private int getRandomMove(String[] board)
	{
		java.util.List<Integer> availableMoves = new java.util.ArrayList<>();
		for(int i = 0; i < 9; i++) {
			if(board[i].equals("")) {
				availableMoves.add(i);
			}
		}
		
		if(availableMoves.isEmpty()) return -1;
		
		int randomIndex = (int)(Math.random() * availableMoves.size());
		return availableMoves.get(randomIndex);
	}
	
	private void toggleSound()
	{
		soundEnabled = !soundEnabled;
		soundToggleButton.setLabel(soundEnabled ? "SOUND ON" : "SOUND OFF");
		
		if(soundEnabled) {
			playToggleSound();
		}
	}
	
	private void changeTheme()
	{
		String[] themes = {"Classic", "Dark", "Ocean", "Sunset", "Forest"};
		int currentIndex = 0;
		
		// Find current theme index
		for(int i = 0; i < themes.length; i++) {
			if(themes[i].equals(selectedTheme)) {
				currentIndex = i;
				break;
			}
		}
		
		// Move to next theme
		currentIndex = (currentIndex + 1) % themes.length;
		selectedTheme = themes[currentIndex];
		
		// Apply new theme
		applyTheme();
		
		// Update all button colors if game is active
		if(gameStarted && !gameOver) {
			for(int i = 0; i < gameButtons.length; i++) {
				if(!gameButtons[i].getLabel().equals("")) {
					Color xColor, oColor;
					switch(selectedTheme) {
						case "Dark":
							xColor = new Color(255, 99, 71);
							oColor = new Color(144, 238, 144);
							break;
						case "Ocean":
							xColor = new Color(255, 20, 147);
							oColor = new Color(0, 191, 255);
							break;
						case "Sunset":
							xColor = new Color(255, 69, 0);
							oColor = new Color(255, 215, 0);
							break;
						case "Forest":
							xColor = new Color(139, 69, 19);
							oColor = new Color(34, 139, 34);
							break;
						default: // Classic
							xColor = new Color(220, 20, 60);
							oColor = new Color(0, 128, 0);
							break;
					}
					
					if(gameButtons[i].getLabel().equals("X")) {
						gameButtons[i].setForeground(xColor);
					} else if(gameButtons[i].getLabel().equals("O")) {
						gameButtons[i].setForeground(oColor);
					}
				}
			}
		}
		
		repaint();
		
		JOptionPane.showMessageDialog(
			this,
			"Theme changed to: " + selectedTheme,
			"Theme Changed",
			JOptionPane.INFORMATION_MESSAGE
		);
	}
	
	private void showPlayerStats()
	{
		StringBuilder stats = new StringBuilder();
		stats.append("PLAYER STATISTICS\n");
		stats.append("==========================================\n\n");
		
		stats.append("Total Games Played: ").append(playerStats.get("totalGames")).append("\n");
		stats.append("Games Won: ").append(playerStats.get("wins")).append("\n");
		stats.append("Games Lost: ").append(playerStats.get("losses")).append("\n");
		stats.append("Games Drawn: ").append(playerStats.get("draws")).append("\n\n");
		
		int total = playerStats.get("totalGames");
		if(total > 0) {
			double winRate = (playerStats.get("wins") * 100.0) / total;
			stats.append("Win Rate: ").append(String.format("%.1f", winRate)).append("%\n");
		}
		
		stats.append("Current Win Streak: ").append(playerStats.get("winStreak")).append("\n");
		stats.append("Best Win Streak: ").append(playerStats.get("bestWinStreak")).append("\n\n");
		
		if(gameStarted && !gameOver) {
			long currentTime = System.currentTimeMillis();
			long gameTime = (currentTime - gameStartTime) / 1000;
			stats.append("Current Game Time: ").append(gameTime).append(" seconds\n");
		}
		
		if(totalGameTime > 0) {
			stats.append("Total Play Time: ").append(totalGameTime / 1000).append(" seconds");
		}
		
		JOptionPane.showMessageDialog(
			this,
			stats.toString(),
			"Player Statistics",
			JOptionPane.INFORMATION_MESSAGE
		);
	}
	
	private void updatePlayerStats(String winner, boolean isDraw)
	{
		playerStats.put("totalGames", playerStats.get("totalGames") + 1);
		
		if(gameStartTime > 0) {
			totalGameTime += (System.currentTimeMillis() - gameStartTime);
		}
		
		if(isDraw) {
			playerStats.put("draws", playerStats.get("draws") + 1);
			playerStats.put("winStreak", 0); // Reset win streak on draw
		} else if(winner.equals(player1Name) || (vsComputer && winner.equals(player1Name))) {
			playerStats.put("wins", playerStats.get("wins") + 1);
			int currentStreak = playerStats.get("winStreak") + 1;
			playerStats.put("winStreak", currentStreak);
			
			if(currentStreak > playerStats.get("bestWinStreak")) {
				playerStats.put("bestWinStreak", currentStreak);
			}
		} else {
			playerStats.put("losses", playerStats.get("losses") + 1);
			playerStats.put("winStreak", 0); // Reset win streak on loss
		}
	}
	
	private void saveGameToFile()
	{
		try {
			String filename = "TicTacToe_Game_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".txt";
			FileWriter writer = new FileWriter(filename);
			
			writer.write("TIC-TAC-TOE GAME RECORD\n");
			writer.write("=======================\n\n");
			writer.write("Date: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "\n");
			writer.write("Players: " + player1Name + " (X) vs " + player2Name + " (O)\n");
			
			if(competitionMode) {
				writer.write("Mode: Competition (" + totalRounds + " rounds)\n");
				writer.write("Current Round: " + currentRound + "\n");
			} else {
				writer.write("Mode: Single Game\n");
			}
			
			if(vsComputer) {
				writer.write("AI Difficulty: " + aiDifficulty + "\n");
			}
			
			writer.write("Theme: " + selectedTheme + "\n");
			writer.write("Sound: " + (soundEnabled ? "Enabled" : "Disabled") + "\n\n");
			
			// Current game state
			writer.write("CURRENT GAME STATE:\n");
			writer.write("-------------------\n");
			for(int row = 0; row < 3; row++) {
				for(int col = 0; col < 3; col++) {
					int index = row * 3 + col;
					String cell = gameButtons[index].getLabel();
					writer.write(cell.isEmpty() ? "_" : cell);
					if(col < 2) writer.write(" | ");
				}
				writer.write("\n");
				if(row < 2) writer.write("---------\n");
			}
			writer.write("\n");
			
			// Game record
			writer.write("MOVE HISTORY:\n");
			writer.write("-------------\n");
			for(String move : gameRecord) {
				writer.write(move + "\n");
			}
			
			// Statistics
			writer.write("\nPLAYER STATISTICS:\n");
			writer.write("------------------\n");
			writer.write("Total Games: " + playerStats.get("totalGames") + "\n");
			writer.write("Wins: " + playerStats.get("wins") + "\n");
			writer.write("Losses: " + playerStats.get("losses") + "\n");
			writer.write("Draws: " + playerStats.get("draws") + "\n");
			writer.write("Win Streak: " + playerStats.get("winStreak") + "\n");
			writer.write("Best Streak: " + playerStats.get("bestWinStreak") + "\n");
			
			writer.close();
			
			JOptionPane.showMessageDialog(
				this,
				"Game saved successfully to: " + filename,
				"Game Saved",
				JOptionPane.INFORMATION_MESSAGE
			);
			
		} catch(IOException e) {
			JOptionPane.showMessageDialog(
				this,
				"Error saving game: " + e.getMessage(),
				"Save Error",
				JOptionPane.ERROR_MESSAGE
			);
		}
	}
	
	// Sound effect methods (simulated with system beeps)
	private void playMoveSound()
	{
		if(soundEnabled) {
			java.awt.Toolkit.getDefaultToolkit().beep();
		}
	}
	
	private void playWinSound()
	{
		if(soundEnabled) {
			// Multiple beeps for win
			for(int i = 0; i < 3; i++) {
				java.awt.Toolkit.getDefaultToolkit().beep();
				try { Thread.sleep(100); } catch(InterruptedException e) {}
			}
		}
	}
	
	private void playDrawSound()
	{
		if(soundEnabled) {
			// Single long beep for draw
			java.awt.Toolkit.getDefaultToolkit().beep();
		}
	}
	
	private void playToggleSound()
	{
		// Always play toggle sound to confirm it's working
		java.awt.Toolkit.getDefaultToolkit().beep();
	}
	
	// Animation method (simple color flash)
	private void animateButton(Button button)
	{
		if(!showAnimations) return;
		
		Color originalBg = button.getBackground();
		button.setBackground(new Color(255, 255, 0)); // Flash yellow
		repaint();
		
		// Use timer to restore original color
		Thread animationThread = new Thread(() -> {
			try {
				Thread.sleep(200);
				button.setBackground(originalBg);
				repaint();
			} catch(InterruptedException e) {}
		});
		animationThread.start();
	}
	
	// AI Methods for Hint System
	private boolean isShowingHint = false;
	
	private void showHint()
	{
		// Prevent multiple hint dialogs
		if(isShowingHint) {
			return;
		}
		
		isShowingHint = true;
		
		if(gameOver) {
			JOptionPane.showMessageDialog(
				this,
				"Game is over! No hints needed.",
				"Game Over",
				JOptionPane.INFORMATION_MESSAGE
			);
			isShowingHint = false;
			return;
		}
		
		int bestMove = getBestMove();
		if(bestMove == -1) {
			JOptionPane.showMessageDialog(
				this,
				"No valid moves available!",
				"No Hints",
				JOptionPane.INFORMATION_MESSAGE
			);
			isShowingHint = false;
			return;
		}
		
		// Highlight the suggested move
		highlightHintMove(bestMove);
		
		// Show hint message
		int row = bestMove / 3 + 1;
		int col = bestMove % 3 + 1;
		String moveDescription = "Row " + row + ", Column " + col;
		
		JOptionPane.showMessageDialog(
			this,
			"Optimal move for " + getCurrentPlayerName() + ":\n" + moveDescription + "\n\nThe suggested button is highlighted in yellow!",
			"💡 Hint for " + getCurrentPlayerName(),
			JOptionPane.INFORMATION_MESSAGE
		);
		
		// Remove highlight after showing hint
		removeHintHighlight();
		isShowingHint = false;
	}
	
	private int getBestMove()
	{
		// Create current board state
		String[] board = new String[9];
		for(int i = 0; i < 9; i++) {
			board[i] = gameButtons[i].getLabel();
		}
		
		int bestScore = Integer.MIN_VALUE;
		int bestMove = -1;
		
		// Try all possible moves
		for(int i = 0; i < 9; i++) {
			if(board[i].equals("")) { // Empty cell
				board[i] = currentPlayer; // Make the move
				int score = minimax(board, 0, false, currentPlayer);
				board[i] = ""; // Undo the move
				
				if(score > bestScore) {
					bestScore = score;
					bestMove = i;
				}
			}
		}
		
		return bestMove;
	}
	
	private int minimax(String[] board, int depth, boolean isMaximizing, String aiPlayer)
	{
		String opponent = aiPlayer.equals("X") ? "O" : "X";
		
		// Check terminal states
		if(checkWinState(board, aiPlayer)) {
			return 10 - depth; // Prefer quicker wins
		}
		if(checkWinState(board, opponent)) {
			return depth - 10; // Prefer slower losses
		}
		if(isBoardFull(board)) {
			return 0; // Draw
		}
		
		if(isMaximizing) {
			int maxEval = Integer.MIN_VALUE;
			for(int i = 0; i < 9; i++) {
				if(board[i].equals("")) {
					board[i] = aiPlayer;
					int eval = minimax(board, depth + 1, false, aiPlayer);
					board[i] = "";
					maxEval = Math.max(maxEval, eval);
				}
			}
			return maxEval;
		} else {
			int minEval = Integer.MAX_VALUE;
			for(int i = 0; i < 9; i++) {
				if(board[i].equals("")) {
					board[i] = opponent;
					int eval = minimax(board, depth + 1, true, aiPlayer);
					board[i] = "";
					minEval = Math.min(minEval, eval);
				}
			}
			return minEval;
		}
	}
	
	private boolean checkWinState(String[] board, String player)
	{
		for(int[] pattern : WIN_PATTERNS) {
			if(board[pattern[0]].equals(player) &&
			   board[pattern[1]].equals(player) &&
			   board[pattern[2]].equals(player)) {
				return true;
			}
		}
		return false;
	}
	
	private boolean isBoardFull(String[] board)
	{
		for(String cell : board) {
			if(cell.equals("")) {
				return false;
			}
		}
		return true;
	}
	
	private void highlightHintMove(int index)
	{
		gameButtons[index].setBackground(new Color(255, 255, 0)); // Yellow highlight
	}
	
	private void removeHintHighlight()
	{
		for(int i = 0; i < gameButtons.length; i++) {
			if(gameButtons[i].getLabel().equals("")) { // Only reset empty buttons
				gameButtons[i].setBackground(new Color(255, 255, 255)); // White background
			}
		}
	}
	
	private boolean isShowingLeaveDialog = false;
	
	private void leaveGame()
	{
		// Prevent multiple leave dialogs
		if(isShowingLeaveDialog) {
			return;
		}
		
		isShowingLeaveDialog = true;
		
		int choice = JOptionPane.showConfirmDialog(
			this,
			"Are you sure you want to leave the current game?\nAll progress will be lost!",
			"Leave Game",
			JOptionPane.YES_NO_OPTION,
			JOptionPane.QUESTION_MESSAGE
		);
		
		if(choice == JOptionPane.YES_OPTION) {
			// Reset all game state completely
			resetCompetition();
		}
		
		isShowingLeaveDialog = false;
	}
	
	private void handleRoundEnd(String winner, boolean isDraw)
	{
		if(competitionMode) {
			// Save current round record only in competition mode
			allRoundsRecord.add(new java.util.ArrayList<>(gameRecord));
			
			// Update competition stats
			String resultText;
			if(isDraw) {
				draws++;
				resultText = "Round " + currentRound + ": Draw";
			} else {
				if(winner.equals(player1Name)) {
					player1Wins++;
				} else {
					player2Wins++;
				}
				resultText = "Round " + currentRound + ": " + winner + " wins";
			}
			roundResults.add(resultText);
			
			// Check if competition is complete
			if(currentRound >= totalRounds) {
				showCompetitionResults();
			} else {
				// Show round result and continue to next round
				showRoundResult(winner, isDraw);
			}
		} else {
			// Single game mode - show normal end game options
			if(isDraw) {
				showDrawMessage();
			} else {
				showWinMessage(winner);
			}
		}
	}
	
	private void showRoundResult(String winner, boolean isDraw)
	{
		String message;
		if(isDraw) {
			message = "Round " + currentRound + " ended in a draw!\n\n";
		} else {
			message = "Round " + currentRound + " winner: " + winner + "!\n\n";
		}
		
		message += "Current Standings:\n";
		message += player1Name + ": " + player1Wins + " wins\n";
		message += player2Name + ": " + player2Wins + " wins\n";
		message += "Draws: " + draws + "\n\n";
		message += "Ready for Round " + (currentRound + 1) + "?";
		
		int choice = JOptionPane.showConfirmDialog(
			this,
			message,
			"Round " + currentRound + " Complete",
			JOptionPane.YES_NO_OPTION,
			JOptionPane.INFORMATION_MESSAGE
		);
		
		if(choice == JOptionPane.YES_OPTION) {
			currentRound++;
			resetGameForNextRound();
		} else {
			System.exit(0);
		}
	}
	
	private void showCompetitionResults()
	{
		StringBuilder results = new StringBuilder();
		results.append("COMPETITION COMPLETE!\n");
		results.append("========================================\n\n");
		
		// Show round by round results
		for(String result : roundResults) {
			results.append(result).append("\n");
		}
		
		results.append("\n========================================\n");
		results.append("FINAL STANDINGS:\n");
		results.append(player1Name).append(": ").append(player1Wins).append(" wins\n");
		results.append(player2Name).append(": ").append(player2Wins).append(" wins\n");
		results.append("Draws: ").append(draws).append("\n\n");
		
		// Determine overall winner
		String overallWinner;
		if(player1Wins > player2Wins) {
			overallWinner = player1Name;
		} else if(player2Wins > player1Wins) {
			overallWinner = player2Name;
		} else {
			overallWinner = "Tie";
		}
		
		results.append("OVERALL WINNER: ").append(overallWinner).append("!\n");
		
		String[] options = {"Play Again", "Show All Moves", "Exit"};
		int choice = JOptionPane.showOptionDialog(
			this,
			results.toString(),
			"Competition Results",
			JOptionPane.YES_NO_CANCEL_OPTION,
			JOptionPane.INFORMATION_MESSAGE,
			null,
			options,
			options[0]
		);
		
		switch(choice) {
			case 0: // Play Again
				resetCompetition();
				break;
			case 1: // Show All Moves
				showAllRoundsMoves();
				showCompetitionResults(); // Return to results after viewing moves
				break;
			case 2: // Exit
			default:
				System.exit(0);
				break;
		}
	}
	
	private void resetGameForNextRound()
	{
		// Reset game buttons
		for(Button button : gameButtons)
		{
			button.setLabel("");
			button.setEnabled(true);
			button.setBackground(new Color(255, 255, 255));
			button.setForeground(new Color(70, 130, 180));
		}
		
		// Reset game state but keep competition data
		moveCount = 0;
		gameOver = false;
		currentPlayer = "X";
		gameRecord.clear();
		currentMoveNumber = 1;
		
		// Update title and status
		createTitleLabel();
		updateStatus(getCurrentPlayerName() + "'s turn");
		repaint();
	}
	
	private void resetCompetition()
	{
		// Reset all game state variables
		moveCount = 0;
		gameOver = false;
		currentPlayer = "X";
		gameRecord.clear();
		currentMoveNumber = 1;
		
		// Reset competition variables completely
		competitionMode = false;
		totalRounds = 1;
		currentRound = 1;
		player1Wins = 0;
		player2Wins = 0;
		draws = 0;
		allRoundsRecord.clear();
		roundResults.clear();
		
		// Reset player names to defaults
		player1Name = "Player 1";
		player2Name = "Player 2";
		
		// Reset VS Computer mode
		vsComputer = false;
		aiDifficulty = "Medium";
		
		// Return to player setup
		removeAll();
		gameStarted = false;
		showPlayerSetupPanel();
		repaint();
	}
	
	private void resetGame()
	{
		// Reset all game buttons
		for(Button button : gameButtons)
		{
			button.setLabel("");
			button.setEnabled(true);
			// Reset button colors to default
			button.setBackground(new Color(255, 255, 255)); // White background
			button.setForeground(new Color(70, 130, 180)); // Steel Blue text
		}
		
		// Reset game state completely
		moveCount = 0;
		gameOver = false;
		currentPlayer = "X";
		
		// Reset game record for new game
		gameRecord.clear();
		currentMoveNumber = 1;
		
		// If not in competition mode, also reset round data
		if(!competitionMode) {
			allRoundsRecord.clear();
			roundResults.clear();
			currentRound = 1;
			player1Wins = 0;
			player2Wins = 0;
			draws = 0;
		}
		
		updateStatus(getCurrentPlayerName() + "'s turn");
	}
	
	private void showGameMoves()
	{
		if(!competitionMode) {
			// Single game mode - show current game moves
			showSingleGameMoves();
		} else {
			// Competition mode - show all rounds moves
			showAllRoundsMoves();
		}
	}
	
	private void showSingleGameMoves()
	{
		if(gameRecord.isEmpty()) {
			JOptionPane.showMessageDialog(
				this,
				"No moves recorded yet!",
				"Game Record",
				JOptionPane.INFORMATION_MESSAGE
			);
			return;
		}
		
		StringBuilder movesText = new StringBuilder();
		movesText.append("GAME RECORD\n");
		movesText.append("Players: ").append(player1Name).append(" (X) vs ").append(player2Name).append(" (O)\n");
		movesText.append("========================================\n\n");
		
		for(String move : gameRecord) {
			movesText.append(move).append("\n");
		}
		
		// Add game result
		movesText.append("\n========================================\n");
		if(gameOver) {
			if(moveCount == 9 && !isWinningState()) {
				movesText.append("RESULT: Draw Game!\n");
			} else {
				String winner = getWinner();
				if(winner != null) {
					String winnerName = winner.equals("X") ? player1Name : player2Name;
					movesText.append("WINNER: ").append(winnerName).append(" (").append(winner).append(")\n");
				}
			}
		} else {
			movesText.append("GAME IN PROGRESS...\n");
			movesText.append("Current Turn: ").append(getCurrentPlayerName()).append(" (").append(currentPlayer).append(")\n");
		}
		
		movesText.append("Total Moves: ").append(gameRecord.size());
		
		JOptionPane.showMessageDialog(
			this,
			movesText.toString(),
			"Game Moves Record",
			JOptionPane.INFORMATION_MESSAGE
		);
	}
	
	private void showAllRoundsMoves()
	{
		if(allRoundsRecord.isEmpty()) {
			JOptionPane.showMessageDialog(
				this,
				"No completed rounds yet!",
				"Competition Record",
				JOptionPane.INFORMATION_MESSAGE
			);
			return;
		}
		
		StringBuilder movesText = new StringBuilder();
		movesText.append("COMPETITION RECORD\n");
		movesText.append("Players: ").append(player1Name).append(" (X) vs ").append(player2Name).append(" (O)\n");
		movesText.append("========================================\n\n");
		
		// Show completed rounds only
		for(int i = 0; i < allRoundsRecord.size(); i++) {
			movesText.append("ROUND ").append(i + 1).append(":\n");
			movesText.append("----------------------------------------\n");
			
			for(String move : allRoundsRecord.get(i)) {
				movesText.append(move).append("\n");
			}
			
			if(i < roundResults.size()) {
				movesText.append("\n").append(roundResults.get(i)).append("\n");
			}
			movesText.append("\n");
		}
		
		// Show competition stats
		movesText.append("========================================\n");
		movesText.append("COMPETITION STATUS:\n");
		movesText.append(player1Name).append(": ").append(player1Wins).append(" wins\n");
		movesText.append(player2Name).append(": ").append(player2Wins).append(" wins\n");
		movesText.append("Draws: ").append(draws).append("\n");
		
		if(currentRound <= totalRounds) {
			movesText.append("Completed: ").append(allRoundsRecord.size()).append(" of ").append(totalRounds).append(" rounds");
		} else {
			movesText.append("Competition Complete!");
		}
		
		JOptionPane.showMessageDialog(
			this,
			movesText.toString(),
			"Competition Moves Record",
			JOptionPane.INFORMATION_MESSAGE
		);
	}
	
	private void showWinMessage(String winner)
	{
		String message = "Congratulations! " + winner + " wins!\n\nWhat would you like to do next?";
		
		String[] options = {"Play Again", "Show Moves", "Exit"};
		int choice = JOptionPane.showOptionDialog(
			this,
			message,
			"Game Over - " + winner + " Wins!",
			JOptionPane.YES_NO_CANCEL_OPTION,
			JOptionPane.INFORMATION_MESSAGE,
			null,
			options,
			options[0]
		);
		
		switch(choice) {
			case 0: // Play Again
				resetGame();
				break;
			case 1: // Show Moves
				showGameMoves();
				// After showing moves, ask again what to do
				showWinMessage(winner);
				break;
			case 2: // Exit
			default:
				System.exit(0);
				break;
		}
	}
	
	private void showDrawMessage()
	{
		String message = "It's a draw! Good game!\n\nWhat would you like to do next?";
		
		String[] options = {"Play Again", "Show Moves", "Exit"};
		int choice = JOptionPane.showOptionDialog(
			this,
			message,
			"Game Over - It's a Draw!",
			JOptionPane.YES_NO_CANCEL_OPTION,
			JOptionPane.INFORMATION_MESSAGE,
			null,
			options,
			options[0]
		);
		
		switch(choice) {
			case 0: // Play Again
				resetGame();
				break;
			case 1: // Show Moves
				showGameMoves();
				// After showing moves, ask again what to do
				showDrawMessage();
				break;
			case 2: // Exit
			default:
				System.exit(0);
				break;
		}
	}
	
	// ComponentListener methods for responsive design
	public void componentResized(ComponentEvent e)
	{
		int currentWidth = getWidth();
		int currentHeight = getHeight();
		
		// Only resize if the change is significant (avoid shaking)
		if(Math.abs(currentWidth - lastWidth) > 10 || Math.abs(currentHeight - lastHeight) > 10)
		{
			if(!isResizing)
			{
				isResizing = true;
				
				if(!gameStarted)
				{
					// Resize player setup panel
					removeAll();
					showPlayerSetupPanel();
				}
				else
				{
					// Store current game state
					String[] buttonLabels = new String[9];
					boolean[] buttonStates = new boolean[9];
					Color[] buttonColors = new Color[9];
					
					for(int i = 0; i < gameButtons.length; i++)
					{
						buttonLabels[i] = gameButtons[i].getLabel();
						buttonStates[i] = gameButtons[i].isEnabled();
						buttonColors[i] = gameButtons[i].getForeground();
					}
					
					// Remove all components and recreate them with new sizing
					removeAll();
					createTitleLabel();
					createGameBoard();
					createControlButtons();
					
					// Restore game state
					for(int i = 0; i < gameButtons.length; i++)
					{
						gameButtons[i].setLabel(buttonLabels[i]);
						gameButtons[i].setEnabled(buttonStates[i]);
						gameButtons[i].setForeground(buttonColors[i]);
					}
					
					// Restore winning highlights if game is over
					if(gameOver && isWinningState())
					{
						highlightWinningCombination();
					}
					
					// Update status
					if(gameOver)
					{
						if(moveCount == 9 && !isWinningState())
						{
							updateStatus("It's a draw!");
						}
						else
						{
							String winner = getWinner();
							if(winner != null)
							{
								String winnerName = winner.equals("X") ? player1Name : player2Name;
								updateStatus(winnerName + " wins!");
							}
						}
					}
					else
					{
						updateStatus(getCurrentPlayerName() + "'s turn");
					}
				}
				
				lastWidth = currentWidth;
				lastHeight = currentHeight;
				repaint();
				isResizing = false;
			}
		}
	}
	
	public void componentMoved(ComponentEvent e) {}
	public void componentShown(ComponentEvent e) {}
	public void componentHidden(ComponentEvent e) {}
	
	private boolean isWinningState()
	{
		return checkWin("X") || checkWin("O");
	}
	
	private String getWinner()
	{
		if(checkWin("X")) return "X";
		if(checkWin("O")) return "O";
		return null;
	}
	
	private void highlightWinningCombination()
	{
		for(int[] pattern : WIN_PATTERNS)
		{
			if(gameButtons[pattern[0]].getLabel().equals(gameButtons[pattern[1]].getLabel()) &&
			   gameButtons[pattern[1]].getLabel().equals(gameButtons[pattern[2]].getLabel()) &&
			   !gameButtons[pattern[0]].getLabel().equals(""))
			{
				highlightWinningButtons(pattern);
				break;
			}
		}
	}
}
class TicTacToe
{
	public static void main(String[] args)
	{
		TicTacToeGame game = new TicTacToeGame();
		game.setVisible(true);
		
		// Set initial window size but allow resizing
		game.setSize(600, 700);
		game.setMinimumSize(new Dimension(550, 650)); // Set minimum size
		game.setLocation(100, 100);
		
		// Add window closing event
		game.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
	}
}

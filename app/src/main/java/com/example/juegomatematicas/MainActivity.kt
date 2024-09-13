package com.example.juegomatematicas

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    // Declaración de las vistas del layout
    private lateinit var tvPlayer1Name: TextView
    private lateinit var tvPlayer1Score: TextView
    private lateinit var tvQuestion: TextView
    private lateinit var etAnswer: EditText
    private lateinit var btnSubmit: Button
    private lateinit var tvPlayer2Name: TextView
    private lateinit var tvPlayer2Score: TextView
    private lateinit var tvTimer: TextView
    private lateinit var btnRetry: Button
    private lateinit var modalLayout: View
    private lateinit var tvWinner: TextView
    private lateinit var tvPlayer1FinalScore: TextView
    private lateinit var tvPlayer2FinalScore: TextView
    private lateinit var btnCloseModal: Button

    // Variables de estado del juego
    private var player1Score = 0
    private var player2Score = 0
    private var currentPlayer = 1
    private var correctAnswer = 0
    private var level = 1
    private var player1Turns = 0
    private var player2Turns = 0
    private var maxTurns = 5 // Número de turnos por jugador antes de finalizar el juego

    // Temporizador del juego
    private lateinit var timer: CountDownTimer
    private var timeLeft = 30000L // 30 segundos por turno

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Asignación de las vistas a las variables
        tvPlayer1Name = findViewById(R.id.tvPlayer1Name)
        tvPlayer1Score = findViewById(R.id.tvPlayer1Score)
        tvQuestion = findViewById(R.id.tvQuestion)
        etAnswer = findViewById(R.id.etAnswer)
        btnSubmit = findViewById(R.id.btnSubmit)
        tvPlayer2Name = findViewById(R.id.tvPlayer2Name)
        tvPlayer2Score = findViewById(R.id.tvPlayer2Score)
        tvTimer = findViewById(R.id.tvTimer)
        btnRetry = findViewById(R.id.btnRetry)
        modalLayout = findViewById(R.id.modalLayout)
        tvWinner = findViewById(R.id.tvWinner)
        tvPlayer1FinalScore = findViewById(R.id.tvPlayer1FinalScore)
        tvPlayer2FinalScore = findViewById(R.id.tvPlayer2FinalScore)
        btnCloseModal = findViewById(R.id.btnCloseModal)

        // Definición de los listeners de los botones
        btnSubmit.setOnClickListener { checkAnswer() }
        btnRetry.setOnClickListener { startGame() }
        btnCloseModal.setOnClickListener { modalLayout.visibility = View.GONE }

        // Inicio del juego
        startGame()
    }

    /**
     * Inicia el juego, estableciendo los valores iniciales
     * y generando la primera pregunta.
     */
    private fun startGame() {
        // Reiniciar las puntuaciones, turnos y jugador actual
        player1Score = 0
        player2Score = 0
        player1Turns = 0
        player2Turns = 0
        currentPlayer = 1
        level = 1
        timeLeft = 30000L  // Reiniciar el tiempo
        tvPlayer1Score.text = "Puntuación: 0"
        tvPlayer2Score.text = "Puntuación: 0"
        btnSubmit.isEnabled = true
        btnRetry.visibility = View.GONE
        etAnswer.isEnabled = true

        // Ocultar el modal si estaba visible
        modalLayout.visibility = View.GONE

        // Asegurarse de cancelar cualquier temporizador activo
        if (::timer.isInitialized) {
            timer.cancel()
        }

        // Generar la primera pregunta y comenzar el temporizador
        generateQuestion()
        startTimer()
    }

    /**
     * Genera una nueva pregunta aleatoria, actualizando la
     * vista de la pregunta y guardando la respuesta correcta.
     */
    private fun generateQuestion() {
        val num1 = Random.nextInt(1, 10 * level)
        val num2 = Random.nextInt(1, 10 * level)
        val operation = Random.nextInt(4)

        when (operation) {
            0 -> {
                tvQuestion.text = "$num1 + $num2 = ?"
                correctAnswer = num1 + num2
            }
            1 -> {
                tvQuestion.text = "$num1 - $num2 = ?"
                correctAnswer = num1 - num2
            }
            2 -> {
                tvQuestion.text = "$num1 * $num2 = ?"
                correctAnswer = num1 * num2
            }
            3 -> {
                tvQuestion.text = "${num1 * num2} / $num1 = ?"
                correctAnswer = num2
            }
        }
    }

    /**
     * Verifica la respuesta del usuario, actualizando la
     * puntuación y el nivel de dificultad si es correcta.
     */
    private fun checkAnswer() {
        val userAnswer = etAnswer.text.toString().toIntOrNull()
        if (userAnswer == correctAnswer) {
            if (currentPlayer == 1) {
                player1Score++
                tvPlayer1Score.text = "Puntuación: $player1Score"
            } else {
                player2Score++
                tvPlayer2Score.text = "Puntuación: $player2Score"
            }
            // Incrementar nivel si es necesario
            if (player1Score % 5 == 0 || player2Score % 5 == 0) {
                level++
            }
        }

        // Registrar turno
        if (currentPlayer == 1) {
            player1Turns++
        } else {
            player2Turns++
        }

        // Verificar si se han completado los turnos
        if (player1Turns >= maxTurns && player2Turns >= maxTurns) {
            endGame()
        } else {
            // Cambiar de jugador
            currentPlayer = if (currentPlayer == 1) 2 else 1
            etAnswer.text.clear()
            generateQuestion()
            startTimer()  // Reiniciar el temporizador para el siguiente jugador
        }
    }

    /**
     * Inicia el temporizador del juego, actualizando la
     * vista del tiempo restante y finalizando el juego
     * cuando se acaba el tiempo.
     */
    private fun startTimer() {
        timer = object : CountDownTimer(timeLeft, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeft = millisUntilFinished
                tvTimer.text = "Tiempo: ${millisUntilFinished / 1000}s"
            }

            override fun onFinish() {
                // Pasar al siguiente jugador si se acaba el tiempo
                if (currentPlayer == 1) {
                    player1Turns++
                } else {
                    player2Turns++
                }

                // Verificar si ambos jugadores han terminado sus turnos
                if (player1Turns >= maxTurns && player2Turns >= maxTurns) {
                    endGame()
                } else {
                    // Cambiar de jugador y reiniciar turno
                    currentPlayer = if (currentPlayer == 1) 2 else 1
                    etAnswer.text.clear()
                    generateQuestion()
                    startTimer()
                }
            }
        }.start()
    }

    /**
     * Finaliza el juego, cancelando el temporizador,
     * actualizando la vista y mostrando el modal con
     * la puntuación actual y la puntuación más alta.
     */
    private fun endGame() {
        timer.cancel()
        tvQuestion.text = "¡Juego terminado!"
        btnSubmit.isEnabled = false
        etAnswer.isEnabled = false
        btnRetry.visibility = View.VISIBLE

        // Mostrar el ganador
        if (player1Score > player2Score) {
            tvWinner.text = "¡Ganador: Jugador 1!"
        } else if (player2Score > player1Score) {
            tvWinner.text = "¡Ganador: Jugador 2!"
        } else {
            tvWinner.text = "¡Empate!"
        }

        tvPlayer1FinalScore.text = "Puntuación del Jugador 1: $player1Score"
        tvPlayer2FinalScore.text = "Puntuación del Jugador 2: $player2Score"
        showModal()
    }

    /**
     * Muestra el modal con la puntuación actual y la
     * puntuación más alta.
     */
    private fun showModal() {
        modalLayout.visibility = View.VISIBLE
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::timer.isInitialized) {
            timer.cancel()
        }
    }
}
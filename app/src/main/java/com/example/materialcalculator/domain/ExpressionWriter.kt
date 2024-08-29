package com.example.materialcalculator.domain

class ExpressionWriter {

    var expression = ""

    fun processAction(action: CalculatorAction) {
        when (action) {
            CalculatorAction.Calculate -> {
                val parser = ExpressionParser(prepareForCalculation())
                val evaluator = ExpressionEvaluator(parser.parse())
                expression = evaluator.evaluate().toString()
            }

            CalculatorAction.Clear -> {
                expression = ""
            }

            CalculatorAction.Delete -> {
                expression = expression.dropLast(1)
            }

            CalculatorAction.Decimal -> {
                if (canEnterDecimal()) expression += "."
            }

            is CalculatorAction.Number -> {
                expression += action.number
            }

            is CalculatorAction.Op -> {
                if (canEnterOperation(action.operation)) expression += action.operation.symbol
            }

            CalculatorAction.Parentheses -> processParentheses()

        }
    }

    private fun prepareForCalculation(): String {
        val newExpression = expression.dropLastWhile {
            it in "$operationSymbols(."
        }
        if (newExpression.isEmpty()) return "0"
        return newExpression
    }

    private fun processParentheses() {
        val openingCount = expression.count { it == '(' }
        val closingCount = expression.count { it == ')' }

        expression += when {
            expression.isEmpty() || expression.last() in "$operationSymbols(" -> "("
            expression.last() in "0123456789)" && openingCount == closingCount -> return
            else -> ")"
        }

    }

    private fun canEnterDecimal(): Boolean {
        return if (expression.isEmpty() || expression.last() in "$operationSymbols.()") false
        else !expression.takeLastWhile {
            it in "0123456789."
        }.contains(".")
    }

    private fun canEnterOperation(operation: Operation): Boolean {
        return if (operation in listOf(Operation.ADD, Operation.SUBTRACT)) {
            when (operation) {
                Operation.ADD -> {
                    expression.isEmpty()
                            || expression.last() !in operationSymbols
                            || expression.last() in "()0123456789"
                }

                Operation.SUBTRACT -> {
                    expression.isEmpty()
                            || expression.last() !in operationSymbols
                            || expression.last() in "()0123456789"
                }

                else -> {
                    expression.isEmpty() || expression.last() in "$operationSymbols()0123456789"
                }
            }

        } else
            expression.isNotEmpty() && expression.last() in "0123456789)"
    }
}
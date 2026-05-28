package com.example.erasmuswallet.data.model

enum class WalletType {
    CARD,
    ACCOUNT,
    CASH,
    INVESTMENT,
    OTHER
}

enum class TransactionType {
    INCOME,
    EXPENSE,
    TRANSFER
}

enum class CategoryGroup {
    OBBLIGATORIE,
    NECESSARIE_FLESSIBILI,
    SVAGO,
    ALTRO
}

enum class IncomeReliability {
    RICEVUTA,
    CONFERMATA,
    STIMATA,
    INCERTA
}

enum class RecurrenceFrequency {
    DAILY,
    WEEKLY,
    MONTHLY,
    YEARLY,
    CUSTOM
}

enum class ScenarioType {
    PRUDENTE,
    REALISTICO,
    OTTIMISTICO
}

enum class BudgetStatus {
    SICURO,
    SOSTENIBILE,
    AL_LIMITE,
    RISCHIOSO,
    NON_SOSTENIBILE
}

enum class SimulationKind {
    SPESA_SINGOLA,
    ABBONAMENTO,
    RATE,
    MASSIMO_SOSTENIBILE
}

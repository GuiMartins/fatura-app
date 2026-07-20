from .base import BankParser, ParsedFatura
from .utils import extrair_mes_ano_referencia, extrair_transacoes_generico


class NubankParser(BankParser):
    banco = "nubank"

    def matches(self, texto: str) -> bool:
        return "nubank" in texto.lower()

    def parse(self, texto: str) -> ParsedFatura:
        mes, ano = extrair_mes_ano_referencia(texto)
        transacoes = extrair_transacoes_generico(texto, ano)
        return ParsedFatura(
            banco=self.banco,
            mes_referencia=mes,
            ano_referencia=ano,
            transacoes=transacoes,
        )

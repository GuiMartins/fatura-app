from .base import BankParser, ParsedFatura
from .utils import extrair_mes_ano_referencia, extrair_transacoes_generico


class ItauParser(BankParser):
    banco = "itau"

    def matches(self, texto: str) -> bool:
        texto_lower = texto.lower()
        return "itau" in texto_lower or "itaú" in texto_lower

    def parse(self, texto: str) -> ParsedFatura:
        mes, ano = extrair_mes_ano_referencia(texto)
        transacoes = extrair_transacoes_generico(texto, ano)
        return ParsedFatura(
            banco=self.banco,
            mes_referencia=mes,
            ano_referencia=ano,
            transacoes=transacoes,
        )

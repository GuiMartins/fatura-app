from .base import BankParser, ParsedFatura
from .utils import extrair_mes_ano_referencia, extrair_transacoes_generico


class MercadoPagoParser(BankParser):
    banco = "mercadopago"

    def matches(self, texto: str) -> bool:
        texto_lower = texto.lower()
        return "mercado pago" in texto_lower or "mercadopago" in texto_lower

    def parse(self, texto: str) -> ParsedFatura:
        mes, ano = extrair_mes_ano_referencia(texto)
        transacoes = extrair_transacoes_generico(texto, ano)
        return ParsedFatura(
            banco=self.banco,
            mes_referencia=mes,
            ano_referencia=ano,
            transacoes=transacoes,
        )

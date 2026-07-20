import pdfplumber

from .base import ParsedFatura
from .bradesco import BradescoParser
from .itau import ItauParser
from .mercadopago import MercadoPagoParser
from .nubank import NubankParser

PARSERS = [NubankParser(), ItauParser(), BradescoParser(), MercadoPagoParser()]


class BancoNaoIdentificadoError(Exception):
    pass


def extrair_texto_pdf(pdf_bytes: bytes) -> str:
    import io

    with pdfplumber.open(io.BytesIO(pdf_bytes)) as pdf:
        return "\n".join(page.extract_text() or "" for page in pdf.pages)


def processar_fatura(pdf_bytes: bytes) -> ParsedFatura:
    texto = extrair_texto_pdf(pdf_bytes)

    for parser in PARSERS:
        if parser.matches(texto):
            return parser.parse(texto)

    raise BancoNaoIdentificadoError(
        "Nao foi possivel identificar o banco desta fatura. "
        "Bancos suportados: Nubank, Itau, Bradesco, Mercado Pago."
    )

import io

import pdfplumber
from pdfminer.pdfdocument import PDFEncryptionError, PDFPasswordIncorrect

from .base import ParsedFatura
from .bradesco import BradescoParser
from .itau import ItauParser
from .mercadopago import MercadoPagoParser
from .nubank import NubankParser

PARSERS = [NubankParser(), ItauParser(), BradescoParser(), MercadoPagoParser()]


class BancoNaoIdentificadoError(Exception):
    pass


class SenhaIncorretaError(Exception):
    pass


def extrair_texto_pdf(pdf_bytes: bytes, senha: str | None = None) -> str:
    try:
        with pdfplumber.open(io.BytesIO(pdf_bytes), password=senha or "") as pdf:
            return "\n".join(page.extract_text() or "" for page in pdf.pages)
    except (PDFPasswordIncorrect, PDFEncryptionError) as exc:
        raise SenhaIncorretaError(
            "Esta fatura esta protegida por senha. Informe a senha correta."
        ) from exc


def processar_fatura(pdf_bytes: bytes, senha: str | None = None) -> ParsedFatura:
    texto = extrair_texto_pdf(pdf_bytes, senha)

    for parser in PARSERS:
        if parser.matches(texto):
            return parser.parse(texto)

    raise BancoNaoIdentificadoError(
        "Nao foi possivel identificar o banco desta fatura. "
        "Bancos suportados: Nubank, Itau, Bradesco, Mercado Pago."
    )

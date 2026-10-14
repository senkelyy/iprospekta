#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Generateur de codes de licence I-Prospekta (outil d'administration DEMO).

Produit des jetons signes HMAC-SHA256, du meme format que ceux verifies par
l'application (LicenseManager.kt) :

    IPK1.<payloadBase64Url>.<signatureBase64Url>
    payload = {"sub": "<client>", "iat": "YYYY-MM-DD", "exp": "YYYY-MM-DD"}

ATTENTION (securite) :
  - Ceci est une demonstration. La cle HMAC est PARTAGEE (presente aussi dans
    l'app), donc extractible par retro-ingenierie.
  - En PRODUCTION : signer avec une cle privee Ed25519 (ex. Google Tink),
    n'embarquer que la cle PUBLIQUE dans l'app, et conserver la cle privee
    uniquement cote serveur / poste d'administration.

Usage :
  python3 generate_license.py --client "Societe X" --days 30
  python3 generate_license.py --client demo --days 30 --count 3
"""
import argparse
import base64
import datetime
import hashlib
import hmac
import json

# DOIT etre identique a DEMO_SECRET dans LicenseManager.kt
SECRET = b"IPK-DEMO-SECRET-CHANGE-ME-9f2b7c"
PREFIX = "IPK1"


def b64url(data: bytes) -> str:
    return base64.urlsafe_b64encode(data).rstrip(b"=").decode("ascii")


def generate(subject: str, days: int) -> tuple[str, datetime.date]:
    today = datetime.date.today()
    exp = today + datetime.timedelta(days=days)
    payload = {"sub": subject, "iat": today.isoformat(), "exp": exp.isoformat()}
    payload_b64 = b64url(json.dumps(payload, separators=(",", ":")).encode("utf-8"))
    signing_input = f"{PREFIX}.{payload_b64}".encode("ascii")
    signature = hmac.new(SECRET, signing_input, hashlib.sha256).digest()
    return f"{PREFIX}.{payload_b64}.{b64url(signature)}", exp


def main() -> None:
    parser = argparse.ArgumentParser(description="Generer des codes de licence I-Prospekta.")
    parser.add_argument("--client", default="client", help="Identifiant du client")
    parser.add_argument("--days", type=int, default=30, help="Duree de validite en jours (defaut 30)")
    parser.add_argument("--count", type=int, default=1, help="Nombre de codes a generer")
    args = parser.parse_args()

    for i in range(args.count):
        sub = args.client if args.count == 1 else f"{args.client}-{i + 1:03d}"
        code, exp = generate(sub, args.days)
        print(f"# {sub}  (expire le {exp.isoformat()})")
        print(code)
        print()


if __name__ == "__main__":
    main()

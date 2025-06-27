from fastapi import FastAPI, Request
from pydantic import BaseModel
from vaderSentiment.vaderSentiment import SentimentIntensityAnalyzer
from typing import List

app = FastAPI()
analyzer = SentimentIntensityAnalyzer()

class SentimentRequest(BaseModel):
    headlines: List[str]

class SentimentResponse(BaseModel):
    average_score: float
    individual_scores: List[float]

@app.post("/analyze", response_model=SentimentResponse)
def analyze_sentiment(req: SentimentRequest):
    scores = [analyzer.polarity_scores(h)["compound"] for h in req.headlines]
    avg_score = sum(scores) / len(scores) if scores else 0.0
    return SentimentResponse(average_score=avg_score, individual_scores=scores)

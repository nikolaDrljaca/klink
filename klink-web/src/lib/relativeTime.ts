
const formatter = new Intl.RelativeTimeFormat(
    navigator.language,
    {
        numeric: "auto"
    }
);

// Set of constants that represent how many ms per unit
const MINUTE = 60 * 1000;
const HOUR = 60 * MINUTE;
const DAY = 24 * HOUR;
const WEEK = 7 * DAY;
// probably not perfect
const MONTH = 4 * WEEK;
const YEAR = MONTH * 12;

function determineRelativeTimeDiff(x: number, unit: string) {
    if (unit === 'minute' || unit === 'second') return Math.round(x / MINUTE);
    if (unit === 'hour') return Math.round(x / HOUR);
    if (unit === 'day') return Math.round(x / DAY);
    if (unit === 'week') return Math.round(x / WEEK);
    if (unit === 'month') return Math.round(x / MONTH);
    if (unit === 'year') return Math.round(x / YEAR);
}

function determineFormatUnit(timeDiff: number): Intl.RelativeTimeFormatUnit {
    const x = Math.abs(timeDiff);
    if (x < MINUTE) return 'second';
    if (x < HOUR) return 'minute';
    if (x < DAY) return 'hour';
    if (x < WEEK) return 'day';
    if (x < MONTH) return 'week';
    if (x < YEAR) return 'month';
    return 'year';
}

export function formatRelative(
    date: Date | number
) {
    const input = typeof date === 'number' ? new Date(date) : date;
    const now = new Date();
    const diff = input.getTime() - now.getTime();
    const unit = determineFormatUnit(diff);
    const value = determineRelativeTimeDiff(diff, unit);
    return formatter.format(value, unit);
}

export function unixFromReponse(value: number): number {
    return +(new Date(value * 1000));
}

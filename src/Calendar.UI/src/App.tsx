import { useEffect, useState } from 'react';
import { Box, ThemeDefaultProvider } from '@edgmont-ui/react'
import { SearchArea, Header, EventArea } from './components';
import { callApi, callPastApi, searchApi } from './api';
import { inheritedTheme } from './utils';

const App = () => {
  const [events, setEvents] = useState<CalendarEvents[]>([]);
  const [pastEvents, setPastEvents] = useState<CalendarEvents[]>([]);
  const [searchEvents, setSearchEvents] = useState<CalendarEvents[]>([]);
  const [dates, setDates] = useState<any[]>([]);
  const [theme, setTheme] = useState<'light' | 'dark'>(inheritedTheme);

  const changeTheme = () => {
    setTheme(theme === 'light' ? 'dark' : 'light')
  }

  const searchDate = (e: any) => {
    let [start, end] = e;
    if (start && end) {
      setDates([start, end]);
    }
  }

  useEffect(() => {
    const fetchData = async () => {
      const result = await callApi();
      setEvents(result);

      const result2 = await callPastApi();
      setPastEvents(result2);

    };

    fetchData();
  }, []);

  useEffect(() => {
    const fetchData = async () => {
      if (!dates[0] && !dates[1]) {
        return;
      }
      const result = await searchApi(dates[0], dates[1]);
      setSearchEvents(result);
    };

    fetchData();
  }, [dates])

  return (
    <ThemeDefaultProvider theme={theme}>
      <Header theme={theme} changeTheme={changeTheme} />

      <Box mx="10" pt="5" display="grid" gridTemplateColumns="1fr 1fr" gridGap="20px">
        <SearchArea onChange={searchDate} searchEvents={searchEvents} />
        <EventArea events={events} pastEvents={pastEvents} />
      </Box>
    </ThemeDefaultProvider >
  )
}

export default App
